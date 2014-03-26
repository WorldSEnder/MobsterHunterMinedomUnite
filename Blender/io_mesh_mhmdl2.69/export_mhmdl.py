import bpy
import threading
from .filetypes.mhdscr_file import MhDscr
from .filetypes.mhmdl_file import Group
#    export_mode = (
#        ("ALL", "Exports with skeleton and animation if found.", ""),
#        ("NO_ANIM", "Exports with vertexdata only.", ""),
#        ("ANIMATION_ONLY", "Exports the animations only.", ""),
#        ("CURR_ANIMATION", "Exports the current animation only.", "")
#    )

def validObject(object):
    try:
        return (object.type == 'MESH' and
            len(object.data.uv_textures) > 0)
    except:
        return False

def export_active(context, config):
    from .constants import configReassureUvs
    from .constants import configModIdIdent
    
    mhdscr = MhDscr()
    object = context.object
    
    if config[configReassureUvs] == True:
        from io_mesh_mhmdl.constants import OperatorUvPrompt
        exec("bpy.ops.%s({'object': object}, 'INVOKE_DEFAULT', event=event)" % OperatorUvPrompt)
    
    if validObject(object):
        mhdscr += object
        
    customModId = config[configModIdIdent]
    if customModId not in (None, ''):
        mhdscr.modId = customModId
    return [mhdscr]

def export_selected(context, config):
    from .constants import configReassureUvs
    from .constants import configModIdIdent
    
    list = []
    for obj in (object for object in context.selected_objects if validObject(object)):
        if config[configReassureUvs] == True:
            from .constants import OperatorUvPrompt
            exec("bpy.ops.%s({'object': obj}, 'INVOKE_DEFAULT')" % OperatorUvPrompt)
        mhdscr = MhDscr()
        mhdscr += obj
        customModId = config[configModIdIdent]
        if customModId not in (None, ''):
            mhdscr.modId = customModId
        list.append(mhdscr)
    return list

def export_selected_onefile(context, config):
    from .constants import configReassureUvs
    from .constants import configModIdIdent
    
    mhdscr = MhDscr()
    for obj in (object for object in context.selected_objects if validObject(object)):
        if config[configReassureUvs] == True:
            from .constants import OperatorUvPrompt
            exec("bpy.ops.%s( {'object': obj}, 'INVOKE_DEFAULT')" % OperatorUvPrompt)
        newSGroups = mhdscr.appendObject(obj)
        for subG in newSGroups:
            subG <<= obj.name
        objG = Group(obj.name, [], 0)
        objG.preReqs = newSGroups[:]
        mhdscr.modelObj.subGroups.append(objG)
    # and then we check for the name of the whole construct
    customModId = config[configModIdIdent]
    if customModId not in (None, ''):
        mhdscr.modId = customModId
    return [mhdscr]

def export_layer(context, config):
    from .constants import configReassureUvs
    from .constants import configModIdIdent
    
    def oneLayerLeast(sLayers, oLayers):
        for i, j in zip(sLayers, oLayers):
            if i and j:
                return True
        return False
    
    list = []
    
    for obj in (object for object in context.scene.objects if oneLayerLeast(context.scene.layers, object.layers) and validObject(object)):
        if config[configReassureUvs] == True:
            from .constants import OperatorUvPrompt
            exec("bpy.ops.%s({'object': obj}, 'INVOKE_DEFAULT')" % OperatorUvPrompt)
        mhdscr = MhDscr()
        mhdscr += obj
        customModId = config[configModIdIdent]
        if customModId not in (None, ''):
            mhdscr.modId = customModId
        list.append(mhdscr)
        
    return list

def export_scene(context, config):
    from .constants import configReassureUvs
    from .constants import configModIdIdent
    
    list = []
    for obj in (object for object in context.scene.objects if validObject(object)):
        if config[configReassureUvs] == True:
            from .constants import OperatorUvPrompt
            exec("bpy.ops.%s({'object': obj}, 'INVOKE_DEFAULT')" % OperatorUvPrompt)
        mhdscr = MhDscr()
        mhdscr += obj
        customModId = config[configModIdIdent]
        if customModId not in (None, ''):
            mhdscr.modId = customModId
        list.append(mhdscr)
        
    return list

export_dict = {
    'ACTIVE': export_active,
    'SELECTED': export_selected,
    'SELECTED_ONEFILE': export_selected_onefile,
    'ACTIVE_LAYERS': export_layer,
    'SCENE': export_scene}

def export_mhmdl(context, fileDir, config):
    from .constants import configTypeFrom
    from .constants import configModIdIdent
    export_func = export_dict.get(config[configTypeFrom], lambda c, conf: [])
    
    MHDSCRs = export_func(context, config)
    for mhdscr in MHDSCRs:
        if mhdscr is None:
            continue
        from os import path
        status = mhdscr.export_to_file('%s/' % path.dirname(fileDir), config[configModIdIdent])
        if status != {'FINISHED'}:
            return status
    return {'FINISHED'}

if __name__ == "__main__":
    pass