import bpy
import bmesh
import struct
from io_mesh_mhmdl.filetypes.mhtex_file import MhTex
from ..constants import mhmdlConfReadUVNameFromObject


class MhMdl:
    __maxBindings = 4
    
    def __init__(self):
        self.subGroups = []
        
        self.__vtxList = []
        self.__normList = []
        self.__uvList = []
        self.__indxList = []
        
        self.__boneMtxs = []
        self.__invBoneMatxs = []
        self.__armObj = None
        self.__bindValues = []
        self.__bindIndxs = []
        
    def resetFields(self):
        # use this as the name, will be always the last object's name
        # the subgroups though will all be pushed back
        self.name = ''
        self.subGroups.clear()
        
        self.__vtxList.clear()
        self.__normList.clear()
        self.__uvList.clear()
        self.__indxList.clear()
        
        self.__boneMtxs.clear()
        self.__invBoneMatxs.clear()
        self.__bindValues.clear()
        self.__bindIndxs.clear()
        
        self.__armObj = None
        
    def appendObject(self, obj, properties={mhmdlConfReadUVNameFromObject: True}):
        from ..export_mhmdl import validObject
        if not validObject(obj):
            #not valid, we do nothing
            print('Not valid object was given, returning')
            return [], []
        
        bm = bmesh.new()
        bm.from_mesh(obj.data)
        bmesh.ops.triangulate(bm, faces=bm.faces)
        
        #get the uv mappings
        uv = bm.loops.layers.uv.active # default
        
        if properties[mhmdlConfReadUVNameFromObject]:
            from ..constants import PropObjectUvResultIdentObj as ident
            try:
                #if there is already such a property and we don't get an error
                uvProp = eval('obj.%s' % ident)
                if uvProp == '':
                    from ..constants import OperatorUvPrompt
                    exec("bpy.ops.%s('INVOKE_DEFAULT')" % OperatorUvPrompt)
                    # TODO wait until execution finished
                    uvProp = eval('obj.%s' % ident)
                uv = bm.loops.layers.uv[uvProp]
            except:
                # stores the result in the objects properties
                print('EXCEPT!')
                from ..constants import OperatorUvPrompt
                exec("bpy.ops.%s('INVOKE_DEFAULT')" % OperatorUvPrompt)
                # TODO wait until execution finished
                uvProp = eval('obj.%s' % ident)
                uv = bm.loops.layers.uv[uvProp]
                
        self.name = obj.name
        vtxList = []
        normList = []
        uvList = []
        indxList = []
        boneMtxs = []
        invBoneMtxs = []
        # __bindValues, 4 per vertice, only initialized if we have bones...
        # it needs a lot of allocated space
        bindValues = [] #[[0.0] * 4] for i in range(0, mesh.vertices)
        bindIndxs = [] #[[0] * 4] for i in range(0, mesh.vertices)
        #for each material we get a subgroup
        from .mhtex_file import is_valid_material
        subGroups = []
        textures = []
        
        from collections import defaultdict
        matIndxToSubGIndx = defaultdict(lambda: -1)
        currentIndx = 0
        for i, mat in enumerate(obj.material_slots):
            if is_valid_material(mat):
                subGroups.append(Group(mat.name, [], texIndx=currentIndx))
                textures.append(MhTex(material))
                matIndxToSubGIndx.update({i: currentIndx})
                currentIndx += 1
        # append a faked subgroup makes appending easier if the texture the vertex is assigned to is invalid/matIndxToSubGIndx returns -1
        subGroups.append(Group("suchAFake.fake", []))
        del currentIndx
        
        verticesC = len(bm.verts)
        vtxIndxToUvs = [[] for i in range(verticesC)]
        vtxIndxToIndxList = [[] for i in range(verticesC)]
        nextVtxIdx = 0
        #for each face
        for tri in bm.faces:
            polygonIndxs = []
            for loop, vtx in zip(tri.loops, tri.verts):
                #set up vars
                vtxId = vtx.index
                vtxUvMeta = loop[uv]
                # that will get checked against existing indices
                indexInList = nextVtxIdx
                #search for almost equal uvs
                for i, uvMapping in enumerate(vtxIndxToUvs[vtxId]):
                    if (uvMapping - vtxUvMeta.uv).length < 0.00001:
                        # found one!!
                        indexInList = vtxIndxToIndxList[vtxId][i]
                        break
                # has not existed yet -> append
                else:
                    #inner mappings
                    vtxIndxToUvs[vtxId].append(vtxUvMeta.uv)
                    vtxIndxToIndxList[vtxId].append(indexInList)
                    #outer mappings
                    vtxList.append(tuple(vtx.co))
                    normList.append(tuple(vtx.normal))
                    uvList.append(tuple(vtxUvMeta.uv))
                    
                    nextVtxIdx += 1
                #append or derived mapping to the face's vertex-list
                polygonIndxs.append(indexInList)
            #end of index-loop
            indxList += polygonIndxs
            # insert in texture's sbGroup
            subGroups[matIndxToSubGIndx[tri.material_index]].indeces += polygonIndxs
            
        del vtxIndxToUvs
        del vtxIndxToIndxList
        del nextVtxIdx
        #end of tri loop
        
        del uv
        
        # if has modifier armature
        for mod in (m for m in obj.modifiers if m.type == 'ARMATURE' and m.object != None):
            arm = mod.object
            if self.__armObj is not None and arm != self.__armObj:
                continue # can't read for two different armatures
            bones = arm.data.bones
            for bone in bones:
                # we have to change from major-coloum to major row...
                matLoc = bone.matrix_local
                boneMtxs.append([[i, j, k] for i, j, k in zip(matLoc[0], matLoc[1], matLoc[2])])
                matLocInv = bone.matrix_local.inverted()
                invBoneMtxs.append([[i, j, k] for i, j, k in zip(matLocInv[0], matLocInv[1], matLocInv[2])])
            
            bindCount = min(self.__maxBindings, len(boneMtxs))
            bindValues = [[0.0] * bindCount for i in range(verticesC)]
            bindIndxs = [[0] * bindCount for i in range(verticesC)]
            
            for boneG, boneIndx in ((boneG, bones.find(boneG.name)) for boneG in obj.vertex_groups if bones.find(boneG.name) > -1):
                for vtxIdx in (vert.index for vert in bm.verts):
                    bindValue = boneG.weight(vtxIdx)
                    currIdx = boneIndx
                    for c in range(bindCount):
                        if bindValue > bindValues[vtxIdx][c]: #guarantees for correct bindIndxs
                            # swap bind value
                            tmpValue = bindValues[vtxIdx][c]
                            bindValues[vtxIdx][c] = bindValue
                            bindValue = tmpValue
                            #swap bind boneIndx
                            tmpIdx = bindIndxs[vtxIdx][c]
                            bindIndxs[vtxIdx][c] = currIdx
                            currIdx = tmpIdx
                        #else do nothing
                    #all bindings updated / pushed
                #all verts updated
            # all propable groups processed
            break
        else:
            # meaning that no armature modifier matched
            # reset all armatures etc, set the arm to something invalid
            self.__armObj = 5
            self.__boneMtxs.clear()
            self.__invBoneMatxs.clear()
            self.__bindValues.clear()
            self.__bindIndxs.clear()
        # offset the whole thing
        offset = len(self.__vtxList)
        for i in range(len(indxList)):
            indxList[i] += offset
        
        for subG in subGroups:
            for i in range(len(subG.indeces)):
                subG.indeces[i] += offset
        
        offset = len(self.__boneMtxs)
        for bindsVtx in bindIndxs:
            for i in range(len(bindsVtx)):
                bindVtx[i] += offset # ints are sadly immutable... nothing pythonic I could imagine
        
        del offset
        
        self.__vtxList += vtxList
        self.__uvList += uvList
        self.__normList += normList

        self.__indxList += indxList
        self.__boneMtxs += boneMtxs
        self.__invBoneMatxs += invBoneMtxs
        
        self.__bindValues += bindValues
        self.__bindIndxs += bindIndxs
        
        # cut off the faked Group at the end
        subGroups = subGroups[:-1]
        # and I even return newly read subGroups
        self.subGroups += subGroups
        
        bm.free()
        
        return subGroups, textures
            
    def setObject(self, obj, options={mhmdlConfReadUVNameFromObject: True}):
        self.resetFields()
        return self.appendObject(obj, properties=options)
        
    def __iadd__(self, other):
        if isinstance(other, tuple) or isinstance(other, list):
            for o in iter(other):
                self += o # allows multiple tuples packed in one etc...
        else:
            self.appendObject(other)
        return self
    
    def export_to_file(self, baseDir):
        '''
        The outputted file looks like this
        ----------------------
        | Header             |
        | GeometryDescription|
        | facesAsIndeces     |
        | Bones              |
        | Bindings           |
        | sub-groups         |
        ----------------------
        
        ~~~HEADER: 32 bytes
        -------------------------------
        | length   | type  | function
        -------------------------------
        | [0:2]    | short | magic number (0xe69d)
        | [2:6]    | uint  | version
        | [6:10]   | uint  | vertex count
        | [10:14]  | uint  | index count
        | [14:15]  | byte  | bone count
        | [15:16]  | byte  | sub-group count
        | [16:32]  | byte  | padding
        -------------------------------
        
        ~~~GEOMETRYDESCRIPTION: 32*vertexCount bytes
        ----------------------
        | positions          |
        | uv-mappings        |
        | normals            |
        ----------------------
        
            ~~~POSITIONS: 12*vertexCount bytes
            -------------------------------
            | length   | type     | function
            -------------------------------
            | [x:x+12] | float[3] | position of the vertex as x, y, z
            -------------------------------
            
            ~~~UV-MAPPINGS: 8*vertexCount bytes
            -------------------------------
            | length   | type    | function
            -------------------------------
            | [x:x+8] | float[2] | uv-mapping of the vertex as u, v
            -------------------------------
            
            ~~~NORMALS: 12*vertexCount bytes
            -------------------------------
            | length   | type     | function
            -------------------------------
            | [x:x+12] | float[3] | normal of the vertex as x, y, z
            -------------------------------
            
        ~~~FACESASINDECES: 12*indexCount
        -------------------------------
        | length   | type    | function
        -------------------------------
        | [x:x+12] | int[3]  | a triangle of the mesh represented as vertex-indeces
        -------------------------------
            
        ~~~BONES: 96*boneCount
        If the determinant of one of the bone-matrixes 4x4 representation
        is not +-1 then this can lead to undefined behavior

        If the inverse matrix isn't correctly computed this will
        lead to undefined behavior
        -------------------------------
        | length      | type      | function
        -------------------------------
        | [x:x+96]    | double[12] | the bone-matrix 3x4, major-row format
        | [x+96:x+192] | double[12] | the inverse of the above matrix, faster computation
        -------------------------------
        
        __maxBindings is defaulted to 4
        bC = bindCount = min(__maxBindings, boneCount)
        ~~~BINDINGS: bC*5*boneCount
        If two bindings of one vertex have been mapped to the same bone
        this will eventually lead to undefined behavior
        They are zipped together as value-index-pairs
        -------------------------------
        | length    | type   | function
        -------------------------------
        | [x:x+4]   | float  | the binding to the bone specified below, range [0...1]
        | [x+4:x+5] | byte   | the index of the bones the vertex is binded to
        -------------------------------
        
        groupLength = see #Group#toByteBuffer()
        ~~~SUB-GROUPS: see groupCount*groupLength
        --------------
        | group1     |
        | group2     |
        | ....       |
        --------------
        '''
        vtxCount = len(self.__vtxList)
        boneCount = len(self.__boneMtxs)
        if len(self.subGroups) > 256:
            return # or throw
        if boneCount > 256:
            return # or throw
        header = struct.pack('>2B3I2B16x', # 2 bytes, 3 integers, 2 bytes, 16 padding
                             0xe6, 0x9d, #magic nbr 
                             1, # version
                             vtxCount, 
                             int(len(self.__indxList)/3), # face-count not indexcount
                             boneCount,
                             len(self.subGroups))
        
        posBytesBuff = bytearray(vtxCount*12)
        for i, vtx in enumerate(self.__vtxList):
            struct.pack_into('>3f',
                             posBytesBuff,
                             i*12,
                             *vtx)
        
        uvBytessBuff = bytearray(vtxCount*8)
        for i, uv in enumerate(self.__uvList):
            struct.pack_into('>2f',
                             uvBytessBuff,
                             i*8,
                             *uv)
        
        normBytesBuff = bytearray(vtxCount*12)
        for i, norm in enumerate(self.__normList):
            struct.pack_into('>3f',
                             normBytesBuff,
                             i*12,
                             *norm)
        
        indxBytesBuff = struct.pack('>%si' % len(self.__indxList), *self.__indxList)
        
        boneBytesBuff = bytearray(boneCount*96)
        for i, (boneMat, invBoneMat) in enumerate(zip(self.__boneMtxs, self.__invBoneMatxs)):
            struct.pack_into('>12d12d',
                             boneBytesBuff,
                             i * 96,
                             *( coord for 
                                    tuple in zip((coord for row in boneMat for coord in row), 
                                        (coord for row in invBoneMat for coord in row)) 
                                for coord in tuple)
                             )
        
        storedBindings = min(self.__maxBindings, boneCount)
        bindBytesBuff = bytearray(vtxCount*storedBindings*5) # 1 float + 1 byte per binding
        for i, values in enumerate(self.__bindValues):
            struct.pack_into('>%sf' % storedBindings, bindBytesBuff, i*storedBindings*4, *values)
        for i, indxs in enumerate(self.__bindIndxs):
            struct.pack_into('>%sB' % storedBindings, bindBytesBuff, vtxCount*storedBindings*4 + i*storedBindings, *indxs)
        
        gBuff = b''
        gLinkageBuff = b''
        for i, subG in enumerate(self.subGroups):
            gBuff += subG.encode()
            for linkedG in subG.preReqs:
                indxLinkedG = self.subGroups.index(linkedG)
                # subGs indeces
                gLinkageBuff += struct.pack('>B', indxLinkedG)
            # no group can be linked to itself -> endOfLinks marker
            gLinkageBuff += struct.pack('>B', i)
        
        subPath = 'models/%s.mhmdl' % self.name
        
        import os
        path = '%s/%s' % (baseDir, subPath)
        os.makedirs(os.path.dirname(path), exist_ok=True)
        
        file = open(path, mode='wb')
        
        file.write(header)
        file.write(posBytesBuff)
        file.write(uvBytessBuff)
        file.write(normBytesBuff)
        file.write(indxBytesBuff)
        file.write(boneBytesBuff)
        file.write(bindBytesBuff)
        file.write(gBuff)
        file.write(gLinkageBuff)
        
        file.close()
        
        return subPath
        
class Group:
    def __init__(self, name="default", indeces=[], texIndx=-1):
        #name of the group
        self.name = name
        self.textureIndex = texIndx
        self.preReqs = []
        #index of faces to render
        self.indeces = indeces[:]
        
    """makes this elements name a 'subelement' of the given parent by left-pushing the name with 'parentName.'"""
    def __lshift__(self, parentName):
        ins = Group('%s.%s' % (parentName, self.name), self.indeces[:])
        ins.preReqs = self.preReqs[:]
        return ins
    
    def encode(self):
        import struct
        # 0xff is no valid utf-8 codepoint :)
        return self.name.encode(encoding='utf-8', errors='strict') + \
            struct.pack('>BBI%sI' % len(self.indeces),
                        0xff, # signals end of stringbytes
                        self.textureIndex,
                        len(self.indeces)/3, #face count not index-count 
                        *self.indeces)
    
    def __rlshift__(self, other):
        return self.__lshift__(other)
        
    """makes this elements name a 'subelement' of the given parent by left-pushing the name with 'parentName.'"""
    def __ilshift__(self, parentName):
        self.name = '%s.%s' % (parentName, self.name)
        return self
    
    """cuts off count parents from the name. note that negatives are allowed. will select the last n then"""
    def __rshift__(self, count):
        ret = Group('.'.join(self.name.split(sep='.', maxsplit=count)[count:]), # new name
                     self.indeces[:])
        ret.preReqs = self.preReqs[:]
        return ret
    
    def __rrshift__(self, count):
        return self.__rshift__(count)
    
    """cuts off count parents from the name. note that negatives are allowed. will select the last n then"""
    def __irshift__(self, count):
        self.name = '.'.join(self.name.split(sep='.', maxsplit=count)[count:])
        return self
    
    def __iadd__(self, other):
        try:
            for e in other:
                self += e
        except TypeError:
            if isinstance(other, Group) and not self.preReqs.count(other):
                self.preReqs.append(other)
        return self