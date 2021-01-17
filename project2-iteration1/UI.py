import shutil
import tkinter
from tkinter import *
from tkinter import filedialog
import os

from openpyxl import load_workbook


class UI(object):
    def __init__(self, Controller):
        self.remote = Controller
        self.window = tkinter.Tk()
        self.window.title('MARMARA UNIVERSTY ZOOM POLLS ANALYZER')
        self.window.geometry("1200x900")
        self.window.resizable(width=0, height=0)
        self.button_explore = Button(self.window,
                                     text="ADD NEW POLL",
                                     command=(self.addNewPoll))
        self.statButton = Button(self.window,
                                 text="Poll Stat",
                                 command=(self.pollStatistic))

        self.button_explore.pack()
        self.statButton.pack()

        self.window.mainloop()

    def addNewPoll(self):
        filename = filedialog.askopenfilename(initialdir="/",
                                              title="Select a File",
                                              filetypes=(("Text files",
                                                          "*.txt*"),
                                                         ("all files",
                                                          "*.*")))

        print(os.getcwd(), filename)
        shutil.copy(filename, os.getcwd())

        self.remote.startSystem()

    def pollStatistic(self):
        penc = Toplevel(self.window)
        penc.title("Pie")
        penc.mainloop()
