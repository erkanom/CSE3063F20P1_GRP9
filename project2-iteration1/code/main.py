import Controller
from UI import UI
from openpyxl import Workbook, load_workbook

if __name__ == '__main__':
    workbook = Workbook()
    sheet = workbook.active
    workbook.save(filename="Global_LOG.xlsx")
    myCont = Controller.Controller()
    myCont.startSystem()
    ui=UI(myCont)


