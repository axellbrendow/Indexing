#doxygen -g doxyconfig.txt

echo "DOXYFILE_ENCODING = UTF-8
PROJECT_NAME = ArvoreB
RECURSIVE = YES
EXTRACT_ALL = YES
OUTPUT_LANGUAGE = Portuguese
OUTPUT_DIRECTORY = ../doc
GENERATE_HTML = YES
GENERATE_LATEX = NO" | Out-File -Encoding "UTF8" doxyconfig.txt
# Caso você esteja num sistema unix, substitua a linha a cima por:
# GENERATE_LATEX = NO" > doxyconfig.txt

doxygen doxyconfig.txt