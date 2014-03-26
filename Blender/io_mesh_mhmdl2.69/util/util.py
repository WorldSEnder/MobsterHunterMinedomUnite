def filepath_from_assetsRes(assetsRes):
    def splitPath(assetsResPath):
        index = assetsResPath.index(':')
        if index == 0 :
            return ['minecraft', assetsResPath[1:]]
        elif index > 0 :
            return [assetsResPath[0:index], assetsResPath[index+1:]]
        return ['minecraft', assetsResPath]
    
    fields = splitPath(assetsRes)
    modId = fields[0]
    resPath = fields[1]
    return '/assets/' + modId + '/' + resPath