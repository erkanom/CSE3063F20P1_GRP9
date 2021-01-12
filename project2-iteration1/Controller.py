import xlrd
from Student import Student


class Controller(object):
    def __init__(self):
        print("[LOG] Controller Object Created")
        self.studentList=[]

    #can be improved to getting extra arguments
    def readStudent(self):
        workbook = xlrd.open_workbook('CES3063_Fall2020_rptSinifListesi.xls')
        worksheet = workbook.sheet_by_name('rptSinifListesi')

        #For every row control column b  digit or something else
        for row in range(231):
            if isinstance(worksheet.cell(row, 1).value,float):
                self.studentList.append(Student(worksheet.cell(row, 2).value,worksheet.cell(row, 4).value,worksheet.cell(row, 7).value,worksheet.cell(row, 10).value))






