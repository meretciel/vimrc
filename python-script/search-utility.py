import os
import sys

OP_FILE = '--file'
OP_NO_TEST = '--notest'
OP_WITH_TEST = '--withtest'
OP_RAW = '--raw'
OP_DIRECTORY = '--directory'
OP_NO_LINE_NUMBER = '--nolinenumber'


VALID_OPTIONS = [ 
    OP_FILE,
    OP_NO_TEST,
    OP_RAW,
    OP_DIRECTORY,
    OP_WITH_TEST,
    OP_NO_LINE_NUMBER,
]


def parseOptions(args):
    options =  [x for x in args if x.startswith('--')]
    assert len(options) != 0, "Invalid arguments. "
    assert all(x in VALID_OPTIONS for x in options), "Invalid options. The valid options are {) ".format(VALID_OPTIONS)
    return options

def parsePattern(args):
    result = [x for x in args if not x.startswith('--')]
    assert len(result) == 1, "Wrong number of search pattern. The search command expects exactly one search pattern."
    return result[0]

def translate(pattern):
    return pattern.replace(' ', '[[:space:]]')\
            .replace('(', '\(')\
            .replace(')', '\)')

def exitProgramWithMessage():
    print("Invalid argument. Usage search-util [--file] <pattern>")
    exit(1)

if __name__ == '__main__':
    args = sys.argv

    if len(args) == 1:
        exitProgramWithMessage()

    if len(args) == 2:
        pattern = translate(args[1])
        os.system('grep -irn "{}" . | grep -v /tst/com/ | cat -n'.format(pattern))

    else:
        # We must have the optional arguments in this case
        options = parseOptions(args)
        pattern = parsePattern(args[1:])
        commands = []

        if not OP_RAW in options:
            pattern = translate(pattern)

        if OP_FILE in options:
            commands.append('find . -type f -iname "*{}*"'.format(pattern))
        elif OP_DIRECTORY in options:
            commands.append('find . -type d -iname "*{}*"'.format(pattern))
        else:
            commands.append('grep -irn "{}" .'.format(pattern))

        if OP_NO_TEST in options or not OP_WITH_TEST in options:
            commands.append("grep -v /tst/com/")

        if not OP_NO_LINE_NUMBER in options:
            commands.append('cat -n')

        commandToExecute = ' | '.join(commands)
        os.system(commandToExecute)

        
