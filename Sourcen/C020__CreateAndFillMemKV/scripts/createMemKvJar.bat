rem Goto the class files
cd ..\out\production\C020__CreateAndFillMemKV\

rem run jar command
"\Program Files\OpenJDK\jdk-20.0.2\bin\jar" --create --file MemKV.jar com\kwerlingit\MemKV.class de\porsche\NewMemKVDB.class

move MemKV.jar ..\..\..\lib\



