import Controller
if __name__ == '__main__':
    myCont= Controller.Controller()
    myCont.readStudent()
    studentList=myCont.studentList
    poolList=myCont.poolList
    myCont.readAttendance()
#Here i will get one more list to raw pool data and i will call start system