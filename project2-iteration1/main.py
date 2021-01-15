import Controller
from UI import UI

if __name__ == '__main__':
    myCont = Controller.Controller()
    myCont.readStudent()
    studentList = myCont.studentList
    poolList = myCont.poolList
    myCont.readAttendance()
    ui = UI()

