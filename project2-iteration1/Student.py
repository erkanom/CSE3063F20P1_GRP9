
class Student(object):

    def __init__(self, StudentNO, StudentFirstName, StudentLastName, StudentInformation):
        self.StudentNO = StudentNO
        self.StudentFirstName = StudentFirstName
        self.StudentLastName = StudentLastName
        self.StudentInformation = StudentInformation

    def getStudentNo(self):
        return self.StudentNO

    def setStudentNo(self,studentNo):
        self.StudentNO = studentNo

    def getStudentFirstName(self):
            return self.studentFirstName

    def setStudentFirstName(self, studentFirstName):
            self.studentFirstName = studentFirstName

    def getStudentLastName(self):
            return self.StudentLastName

    def setStudentLastName(self, StudentLastName):
            self.StudentLastName = StudentLastName

    def getStudentInformation(self):
        return self.StudentInformation

    def setStudentInformation(self, StudentInformation):
        self.StudentInformation = StudentInformation









