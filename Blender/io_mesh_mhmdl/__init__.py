bl_info = {
    "name": "Export Mobster Hunter Model Format (.mhmdl)",
    "author": "Martin Molzer aka WorldsEnder",
    "version": (0, 0, 6),
    "blender": (2, 69, 0),
    "location": "File > Export > MHModel (.mhmdl)",
    "description": "The script exports Blender geometry a format that the mhmu-mod for minecraft can read.",
    "warning": "Under construction! Visit Wiki for details.",
    "wiki_url": "",
    "tracker_url": "",
    "category": "Import-Export"
}

import bpy
# we have to import everything we consider to use
from .types import *

def exp_menu_func(self, context):
    from .types import MHMDLExporter
    self.layout.operator(MHMDLExporter.bl_idname, text='Mobster Hunter Model (.mhmdl)')

def register():
    bpy.utils.register_module(__name__)
    bpy.types.INFO_MT_file_export.append(exp_menu_func)
    
    from bpy.props import StringProperty

    from .constants import PropObjectUvResultIdentObj
    exec('bpy.types.Object.%s = StringProperty(name="MH Exported UV", description="The UVLayer that will get exported")' % PropObjectUvResultIdentObj)
    from .constants import PropSceneExportDirectoryProp
    exec('bpy.types.Scene.%s = StringProperty(name="MH Export Target Dir", description="The directory where the export defaults to", subtype="DIR_PATH")' % PropSceneExportDirectoryProp)

def unregister():
    bpy.utils.unregister_module(__name__)
    bpy.types.INFO_MT_file_export.remove(exp_menu_func)

if __name__ == "__main__":
    register()