import xlrd

from LogAttendance import LogAttendance
from Student import Student
from csv import reader


class Controller(object):
    def __init__(self):
        print("[LOG] Controller Object Created")
        self.studentList=[]
        self.poolList=[]
        self.attendanceList=[]

    #can be improved to getting extra arguments
    def readStudent(self):
        workbook = xlrd.open_workbook('CES3063_Fall2020_rptSinifListesi.xls')
        worksheet = workbook.sheet_by_name('rptSinifListesi')

        #For every row control column b  digit or something else
        for row in range(231):
            if isinstance(worksheet.cell(row, 1).value,float):
                self.studentList.append(Student(worksheet.cell(row, 2).value,worksheet.cell(row, 4).value,worksheet.cell(row, 7).value,worksheet.cell(row, 10).value))
    def readPools(self):
        pass

    def readAttendance(self):
        with open('CSE3063_20201124_Tue_zoom_PollReport.csv', 'r', encoding='utf-8') as attendance:
            # pass the file object to reader() to get the reader object
            attReader = reader(attendance)
            for row in attReader:
                self.attendanceList.append(LogAttendance(row[1], row[2], row[3], row[4], row[5]))


