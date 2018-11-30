set path=%path%;C:\Program Files\Java\jdk1.8.0_101\bin

javac -d bin -cp bin;C:\Users\b1999\lib\postgresql-42.2.5.jar src/com/bxg796/main/*.java

pause
java -cp bin;C:\Users\b1999\lib\postgresql-42.2.5.jar com.bxg796.main.CreateDatabase
pause