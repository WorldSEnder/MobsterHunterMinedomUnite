from .mhmdl_file import MhMdl
from ..constants import mhmdlConfReadUVNameFromObject

class MhDscr:
    validModes = []
    
    #description file
    def __init__(self):
        self.modelObj = MhMdl()
        self.textures = []
        self.animations = []
        
    def setModel(self, mdl):
        self.modelObj = mdl
        
    def appendObject(self, obj,properties={mhmdlConfReadUVNameFromObject: True}):
        subGs, textures = self.modelObj.appendObject(obj, properties)
        self.textures += [tex for tex in textures if not tex in self.textures]
        for subG in subGs:
            subG.textureIndex = self.textures.index(textures[subG.textureIndex])
        return subGs
    
    def __iadd__(self, other):
        self.modelObj += other
        return self
        
    def setObject(self, obj, options):
        self.modelObj.setObject(MhMdl(obj, options))
        
    def export_to_file(self, baseDir, modId):
        folder = '%s/assets/%s/' % (baseDir, modId)
        mdlPath = self.modelObj.export_to_file(folder)
        texPaths = []
        for i, mhTex in enumerate(self.textures):
            texPaths.append(mhTex.export_to_file(folder))
        animPaths = []
        for i, anim in enumerate(self.animations):
            animPaths.append(anim.export_to_file(folder))
            
        import os
        path = '%s/dscrs/%s.mhdscr' % (folder, self.modelObj.name)
        os.makedirs(os.path.dirname(path), exist_ok=True)
        
        file = open(path, mode='w')
        file.write('mdl %s:%s\n' % (modId, mdlPath))
        for texPath in texPaths:
            if texPath is not None:
                file.write('tex %s:%s\n' % (modId, texPath))
        for animPath in animPaths:
            if animPath is not None:
                file.write('anm %s:%s\n' % (modId, animPath))
        file.close()
        return {'FINISHED'}