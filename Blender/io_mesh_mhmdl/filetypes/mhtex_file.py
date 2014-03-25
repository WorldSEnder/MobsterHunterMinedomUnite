
def is_valid_material(material):
    return material.name.isidentifier()

class MhTex:
    def __init__(self, material):
        #the texName as this is an identifier
        self.matName = material.name
        
    def __eq__(self, other):
        return self.matName == other.matName
        
    def export_to_file(self, pathDir):
        from ..util import filepath_from_assetsRes as parseFPath

        # file = open('w', pathDir + parseFPath(resPath))
        # file.write(imgResPath)
        # file.close()
        pass