import sys
import html
import logging
logging.basicConfig(stream=sys.stdout)
log = logging.getLogger("main")
log.setLevel(logging.ERROR)



PATTERN_END = "\\}"
INLINE_PATTERN_MAP = {
    ("\\hl{", PATTERN_END): ("<b>", "</b>"),
    ("\\note{", PATTERN_END): ("<span style=\"color:blue;\">", "</span>"),
    ("\\command{", PATTERN_END): ("<code>", "</code>"),
    ("`", "`"): ("<code>", "</code>"),
    ("\\danger{", PATTERN_END): ("<span style=\"color:red;\">", "</span>"),
    ("\\warning{", PATTERN_END): ("<span style=\"color:orange;\">", "</span>"),
    ("\\info{", PATTERN_END): ("<span style=\"color:cyan;\">", "</span>"),
    ("\\red{", PATTERN_END): ("<span style=\"color:red;\">", "</span>")
}

TEXT = "text"
CODE = "code"
HTML_LINE = "html-line"
HTML_BLOCK = "html-block"
LINK_TEMPLATE = r'<a href="{link}" target="_blank">{content}</a>'
CODE_FIRST_LINE_TEMPLATE = r'<pre style="padding: 0px;"><code class={lang}>'
HEADER_LINE = "header-line"
MATH_BLOCK = "math-block"
MULTI_LINE_EQUATION = "multi-line-equation"



def transformHeader(rawLine):
    line = rawLine.strip();
    if line.startswith('######'):
        return "<h6>" + line[6:] + "</h6>"
    if line.startswith('#####'):
        return "<h5>" + line[5:] + "</h5>"
    if line.startswith('####'):
        return "<h4>" + line[4:] + "</h4>"
    if line.startswith('###'):
        return "<h3>" + line[3:] + "</h3>"
    if line.startswith('##'):
        return "<h2>" + line[2:] + "</h2>"
    if line.startswith('#'):
        return "<h1>" + line[1:] + "</h1>"
    return rawLine


def transformInlineMarker(rawLine):
    k = 0
    prevIndex = -1
    output = []
    # log.info("transforming inline markers: {}".format(rawLine))
    while k < len(rawLine) and k > prevIndex:
        prevIndex = k
        # log.debug("char = {}, k = {}, prevIndex = {}, output = {}".format(rawLine[k], k, prevIndex, output))
        for oldPattern, newPattern in INLINE_PATTERN_MAP.items():
            startIndex = rawLine.find(oldPattern[0], k)
            if startIndex != -1:
                output.append(rawLine[prevIndex: startIndex])
                output.append(newPattern[0])

                endIndex = rawLine.find(oldPattern[1], startIndex + len(oldPattern[0]))
                if endIndex == -1:
                    raise RuntimeError("Mismatched pattern in line: {}".format(rawLine))

                output.append(rawLine[startIndex + len(oldPattern[0]): endIndex])
                output.append(newPattern[1])

                k = endIndex + len(oldPattern[1])


    if k != 0 and k < len(rawLine):
        output.append(rawLine[k:])

    if k != 0:
        return ''.join(output)
    else:
        return rawLine


def transformLink(line):
    k = 0
    prevIndex = -1
    output = []

    while k < len(line) and k > prevIndex:
        prevIndex = k

        startIndex = line.find('@link[', k)
        endIndexOfContent = line.find('](', k)
        endIndex = line.find(')', endIndexOfContent + 2)

        if startIndex != -1 and endIndexOfContent != -1 and endIndex != -1:
            content = line[startIndex + len('@link['): endIndexOfContent]
            link = line[endIndexOfContent + 2: endIndex]
            output.append(line[prevIndex: startIndex])
            output.append(LINK_TEMPLATE.format(content=content, link=link))
            k = endIndex + 1

    if prevIndex != -1 and prevIndex < len(line) and k != len(line):
        output.append(line[prevIndex:])

    if k != 0:
        return ''.join(output)
    else:
        return line


def getIndentationLevel(line):
    k = 0
    while k < len(line) and line[k] == ' ':
        k += 1
    return k


def isOrderedList(line):
    level = getIndentationLevel(line)
    return line[level: level + 2] == '1.'


def isListLine(line):
    if line == '':
        return False
    level = getIndentationLevel(line)
    if level != 0 and level % 4 == 0:
        if level < len(line) and (line[level] in '*-'):
            return True
        return level + 1 < len(line) and line[level:level + 2] == '1.'
    else:
        return False


def addListOpenTag(isOrderedList, output):
    if isOrderedList:
        output.append('<ol>')
    else:
        output.append('<ul>')


def addListCloseTag(isOrderedList, output):
    if isOrderedList:
        output.append('</ol>')
    else:
        output.append('</ul>')


def transformListLine(line, isOrderedList):
    level = getIndentationLevel(line)

    if isOrderedList:
        return '{}<li>{}</li>'.format(' ' * level, line[level + 2:])
    else:
        return '{}<li>{}</li>'.format(' ' * level, line[level + 1:])


def transformList(lines):
    k = 0
    prevIndex = -1
    output = []
    stack = []

    while k < len(lines) and k > prevIndex:
        prevIndex = k
        isInCode = False
        while k < len(lines) and (not isListLine(lines[k]) or isInCode):
            if lines[k].startswith('@code'):
                isInCode = True

            if lines[k].startswith('@end-code'):
                isInCode = False

            k += 1

        output.extend(lines[prevIndex: k])

        if k == len(lines):
            break

        isOrderedListLine = isOrderedList(lines[k])
        stack.append((getIndentationLevel(lines[k]), isOrderedListLine))

        addListOpenTag(isOrderedListLine, output)

        while stack:
            while k < len(lines) and (
                    lines[k].strip() == '' or (isListLine(lines[k]) and getIndentationLevel(lines[k]) == stack[-1][0])):
                if lines[k].strip() == '':
                    k += 1
                else:
                    output.append(transformListLine(lines[k], stack[-1][1]))
                    k += 1

            if k == len(lines):
                while stack:
                    addListCloseTag(stack[-1][1], output)
                    stack.pop()
                break

            if isListLine(lines[k]) and getIndentationLevel(lines[k]) > stack[-1][0]:
                # we are in the next level
                addListOpenTag(isOrderedList(lines[k]), output)
                stack.append((getIndentationLevel(lines[k]), isOrderedList(lines[k])))
            else:
                # exit from the current level
                addListCloseTag(stack[-1][1], output)
                stack.pop()
        output.append("")

    return output


def getCodeLanguage(line):
    result = line.strip().replace('@code[lang=', '')
    result = result.replace(']', '')
    return result


def isHtmlLine(data):
    patterns = ['<h1>', '<h2>', '<h3>', '<h4>', '<h5>', '<h6>', '</br>']
    return any(data.startswith(pattern) for pattern in patterns)


def isHtmlBlock(data):
    patterns = ['<div ', '<p>', '<style>', '<script>', '<ul>', '<ol>', '<table', '<pre>', '<div>', r'\[', '<blockquote']
    return any(data.startswith(pattern) for pattern in patterns)


def getHtmlBlockEnd(data):
    if data.startswith('<div'):
        return '</div>'

    if data.startswith('<p>'):
        return '</p>'

    if data.startswith('<style>'):
        return '</style>'

    if data.startswith('<script>'):
        return '</script>'

    if data.startswith('<ul>'):
        return '</ul>'

    if data.startswith('<ol>'):
        return '</ol>'

    if data.startswith('<table'):
        return '</table>'

    if data.startswith('<pre>'):
        return '</pre>'

    if data == r'\[':
        return r'\]'

    if data.startswith('<blockquote'):
        return '</blockquote>'


def getMode(line):
    data = line.strip();

    # if data.startswith('<pre style="padding: 0px;"><code class='):
    if data.startswith('@code[lang='):
        return CODE
    elif isHtmlLine(data):
        return HTML_LINE
    elif isHtmlBlock(data):
        return HTML_BLOCK
    elif line.startswith('#'):
        return HEADER_LINE
    elif line.startswith("@mathblock") or line.startswith("@mb"):
        return MATH_BLOCK
    elif line.startswith("@multi-line-equation") or line.startswith("@mleq"):
        return MULTI_LINE_EQUATION
    else:
        return TEXT


def maybeMakeItPara(line, output):
    if line.strip() == "":
        output.append("")
        return
    output.append("")
    output.append('<p>')
    output.append(line)
    output.append('</p>')


def transformBlock(lines):
    output = []
    k = 0
    mode = TEXT

    while k < len(lines):

        line = lines[k].rstrip()
        # print("[{}] current mode: {} | line: {} | next mode: {}".format(k, mode, line, getMode(line)))

        if mode == TEXT:
            modeOfCurrentLine = getMode(line)

            if modeOfCurrentLine == TEXT:
                maybeMakeItPara(line, output)
                k += 1
            elif modeOfCurrentLine == HEADER_LINE:
                output.append(transformHeader(line))
                k += 1
            else:
                mode = modeOfCurrentLine
                # will process the same line again
        elif mode == CODE:
            codeLines = []
            # Add line number
            s = k
            codeLanguage = getCodeLanguage(lines[k])
            s += 1
            while s < len(lines) and lines[s].strip() != '@end-code':
                codeLines.append(html.escape(lines[s].rstrip()))
                s += 1

            if s < len(lines):
                codeLines.append(html.escape(lines[s].rstrip()))

            mode = TEXT
            k = s + 1  # move to the next line

            nLines = len(codeLines)
            output.append('<div class="code-box">')
            output.append('<div class="line-number">')
            lineNumbers = []
            for i in range(1, nLines):
                lineNumbers.append('<b>{}</b></br>'.format(i))
            output.append(''.join(lineNumbers))
            output.append('</div>')
            # modify the first line
            firstLine = codeLines[0]
            codeLines[0] = CODE_FIRST_LINE_TEMPLATE.format(lang=codeLanguage) + firstLine
            # add a </code> at the end
            codeLines[-1] = '</code></pre>'
            output.extend(codeLines)
            output.append('</div>')

        elif mode == MATH_BLOCK:
            output.append("<div>")
            output.append("$$")
            s  = k + 1
            while s < len(lines) and lines[s].strip() != "@endmathblock" and lines[s].strip() != "@end-mathblock" and lines[s].strip() != "@end-mb":
                output.append(lines[s])
                s += 1

            output.append("$$")
            output.append("</div>")

            # move to the next line
            mode = TEXT
            k = s + 1

        elif mode == MULTI_LINE_EQUATION:
            output.append("<div>")
            output.append("$$")
            output.append(r"\begin{eqnarray}")

            s  = k + 1
            while s < len(lines) and lines[s].strip() != "@end-multi-line-equation" and lines[s].strip() != '@end-mleq':
                output.append(lines[s])
                s += 1

            output.append(r"\end{eqnarray}")
            output.append("$$")
            output.append("</div>")

            # move to the next line
            mode = TEXT
            k = s + 1


        elif mode == HTML_LINE:
            output.append(line)
            k += 1
            mode = TEXT
        elif mode == HTML_BLOCK:
            s = k
            endPattern = getHtmlBlockEnd(line)
            countOfPattern = 1
            output.append(lines[s].rstrip())
            s += 1

            while s < len(lines) and countOfPattern > 0:
                l = lines[s]

                if isHtmlBlock(l) and getHtmlBlockEnd(l) == endPattern:
                    countOfPattern += 1

                if l.strip() == endPattern:
                    countOfPattern -= 1

                output.append(lines[s].rstrip())
                s += 1
            k = s
            mode = TEXT

        else:
            raise RuntimeError("[ERROR] Unknown mode at line {}: {}".foramt(k, line))

    return output

def printLines(lines):
    for line in lines:
        print(line)

if __name__ == '__main__':
    data = sys.stdin.readlines()
    linesAfterRstrip = list(map(lambda x: x.rstrip(), data))
    # printLines(linesAfterRstrip)

    linesAfterListTransformation = transformList(linesAfterRstrip)

    # printLines(linesAfterListTransformation)

    # linesAfterHeaderTransformation = list(map(transformHeader, linesAfterListTransformation))
    linesAfterInlineTransformation = list(map(transformInlineMarker, linesAfterListTransformation))
    linesAfterLinkTransformation = list(map(transformLink, linesAfterInlineTransformation))
    output = transformBlock(linesAfterLinkTransformation)

    printLines(output)
