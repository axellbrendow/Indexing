echo "DOXYFILE_ENCODING = 'UTF-8'
PROJECT_NAME = 'ArvoreB'
INPUT = '../ArvoreB'
OUTPUT_DIRECTORY = 'doc'
GENERATE_HTML = 'YES'
GENERATE_LATEX = 'NO'" > doxyconfig.txt

doxygen doxyconfig.txt