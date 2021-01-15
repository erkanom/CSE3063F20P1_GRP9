import re
class Student(object):

    def __init__(self, StudentNO, StudentFirstName, StudentLastName, StudentInformation):
        self.StudentNO = StudentNO
        self.StudentFirstName = StudentFirstName
        self.StudentLastName = StudentLastName
        self.studentFullName= StudentFirstName+" "+StudentLastName
        self.StudentInformation = StudentInformation
        self.AttendanceLogs=[]

    def getAttendanceLogs(self):
            return self.AttendanceLogs

    def getStudentNo(self):
        return self.StudentNO

    def setStudentNo(self,studentNo):
        self.StudentNO = studentNo

    def getStudentFirstName(self):
            return self.StudentFirstName

    def setStudentFirstName(self, studentFirstName):
            self.StudentFirstName = studentFirstName

    def getStudentLastName(self):
            return self.StudentLastName

    def setStudentLastName(self, StudentLastName):
            self.StudentLastName = StudentLastName

    def getStudentFullName(self):
            return self.studentFullName

    def setStudentFullName(self,studentFullName):
        self.studentFullName=studentFullName

    def getStudentInformation(self):
        return self.StudentInformation

    def setStudentInformation(self, StudentInformation):
        self.StudentInformation = StudentInformation









