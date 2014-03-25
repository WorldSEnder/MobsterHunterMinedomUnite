import bpy
from bpy.types import Operator, Panel
from bpy.props import StringProperty, EnumProperty, BoolProperty, CollectionProperty

class MHMDLExporterObject(Panel):
    """This exports the current object from the object-properties panel"""
    
    bl_label = "Mobster Hunter"
    bl_space_type = "PROPERTIES"
    bl_region_type = "WINDOW"
    bl_context = "object"
    
    def draw(self, context):
        layout = self.layout
        
        col = layout.column()
        
        from .constants import PropObjectUvResultIdentObj
        col.prop_search(context.object, PropObjectUvResultIdentObj, context.object.data, 'uv_textures', text="UV Layer")
        
        if context.object.data.uv_textures.get(eval('context.object.%s' % PropObjectUvResultIdentObj)) == None:
            col.label(text="No valid uv-map selected", icon='ERROR')
        
        from .constants import PropSceneExportDirectoryProp
        col.prop(context.scene, PropSceneExportDirectoryProp, text='Export directory')
        
        if eval('context.scene.%s' % PropSceneExportDirectoryProp) == '':
            col.label(text="No valid directory specified", icon='ERROR')
        
        col = col.split(0.6)
        from .constants import OperatorExportMhmu
        exportOp = col.operator(OperatorExportMhmu, text="Export object")
        exportOp.directory = eval('context.scene.%s' % PropSceneExportDirectoryProp)
        exportOp.askForUvs = True
        exportOp.type_from = 'ACTIVE'
        exportOp.mode_from = 'ALL'

class MHMDLExporter(Operator):
    """Exports the current object to the Mobster Hunter model format (.mhdscr, .mhmdl, .mhanm)"""
    
    from .constants import OperatorExportMhmu
    bl_idname = OperatorExportMhmu
    bl_label = "Export MHMDL"
    bl_options = {'PRESET'}
    
    directory = StringProperty(subtype='DIR_PATH')
    
    @classmethod
    def maskFromContext(cls, context):
        mask = 0
        activeLayers = context.scene.layers
        # only allow meshes and objects with at least one uv-tex
        for obj in (object for object in context.scene.objects if object.type == 'MESH' and len(object.data.uv_textures) > 0) :  
            if mask == 0b11111:
                break
            if obj.select :
                mask |= 0b110 # enable SELECTED and SELECTED_ONEFILE
            if context.active_object == obj: # selected doesn't mean active... it's blender after all
                mask |= 0b1
            for i in range(0, len(activeLayers)) :
                if activeLayers[i] and obj.layers[i] : # if in at least one active layer
                    mask |= 0b1000 # enable ACTIVE_LAYERS
                    break
            mask |= 0b10000
        return mask
    
    def validTypes(self, context):
        mask = MHMDLExporter.maskFromContext(context)
        
        options = ()
        if mask & 0b1:
            options += (("ACTIVE", "Active", "Only export the active object"),)
        
        if mask & 0b10:
            options += (("SELECTED", "Selected", "Export all selected objects. They will stored in different files."),)
        
        if mask & 0b100:
            options += (("SELECTED_ONEFILE", "Selected: One File", "Export all selected objects. This will disable export with animations."),)
        
        if mask & 0b1000:
            options += (("ACTIVE_LAYERS", "Layer", "Export from all visible layers. Every object gets stored in a different file."),)
        
        if mask & 0b10000:
            options += (("SCENE", "Scene", "Exports the whole scene. 1:1 object to file."),)
        
        return options
    
    @classmethod
    def poll(cls, context):
        return MHMDLExporter.maskFromContext(context) != 0
    
    export_mode = (
        ("ALL", "Everything", "Exports with skeleton and animation if found."),
        ("NO_ANIM", "Only model", "Exports with vertex-data only."),
        ("ALL_ANIMATIONS", "All animations", "Exports the animations only."),
        ("CURR_ANIMATION", "Current Animation", "Exports the active animation only."))
    
    type_from = EnumProperty(name="Export type", 
                                # default="ACTIVE", not when we have a function
                                description="Export which objects?",
                                items=validTypes)
    
    mode_from = EnumProperty(name="Export mode", default="ALL",
                                description="Export in what mode?",
                                items=export_mode)
    
    modid = StringProperty(name="Your modid", description="The same id as you use in forge. The script automatically exports into assertsfolder.",  default="mhmu")
    
    askForUvs = BoolProperty(name="Reask for uv-Layer", description="If you select this option you are asked to select an uv-Layer for each object to export.", default=False)
    
    reassureFilePath = BoolProperty(name="Reask", description="Whatever", default=True)
    
    def draw(self, context):
        layout = self.layout
        col = layout.column()
        col.prop(self, 'type_from')
        col.prop(self, 'mode_from')
        col.prop(self, 'modid')
        col = layout.column()
        # TODO prevent invalid options
        col.prop(self, 'askForUvs')
    
    def execute(self, context):
        # all vars should be correct by now
        from .constants import configTypeFrom
        from .constants import configModeFrom
        from .constants import configModIdIdent
        from .constants import configReassureUvs
        conf = {
            configTypeFrom : self.type_from,
            configModeFrom : self.mode_from,
            configModIdIdent : self.modid,
            configReassureUvs : self.askForUvs
        }
        from .export_mhmdl import export_mhmdl
        ret = export_mhmdl(context, self.directory, conf)
        return ret
        
    def invoke(self, context, event):
        from os import path
        # if invalid directory, we reassure that we got it right
        if self.reassureFilePath or self.directory == '' or self.directory[-1] not in [path.sep, path.altsep] :
            self.filepath = self.directory
            context.window_manager.fileselect_add(self)
        
        # TODO check for invalid states like ACTIVE without a valid uvMap etc....
        return {'RUNNING_MODAL'}
        
class UVLayerPrompt(Operator):
    """Prompts the user for the uv-layer he wishes to select for each object"""
    from .constants import OperatorUvPrompt
    bl_idname = OperatorUvPrompt
    bl_label = "Choose the uv-layer"
    bl_options = {'REGISTER', 'INTERNAL'}

    @classmethod
    def poll(cls, context):
        return (context.object is not None and
                context.object.type == 'MESH')

    def invoke(self, context, event):
        import threading
        eve = threading.Event()
        bpy.ops.mhmu.uvpromptintern({'object': context.object}, 'INVOKE_DEFAULT', ev=eve)
        eve.wait()
        return {'FINISHED'}

    def execute(self, context):
        return {'FINISHED'}
    
class UVLayerPromptINTERN(Operator):
    """Internal representation of the uv-prompter. This does not wait for the prompt to finish"""
    bl_idname = 'mhmu.uvpromptintern'
    bl_label = "Choose the uv-layer"
    bl_options = {'REGISTER', 'INTERNAL'}
    
    # does NOT work
    ev = None
    
    def invoke(self, context, event):
        return context.window_manager.invoke_props_dialog(self)

    def draw(self, context):
        layout = self.layout
        col = layout.column()
        
        from .constants import PropObjectUvResultIdentObj as ident
        col.prop_search(context.object, ident, context.object.data, 'uv_textures', text="UV Layer")

    def execute(self, context):
        if self.ev is not None:
            self.ev.set()
        return {'FINISHED'}
    