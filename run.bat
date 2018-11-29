set path=%path%;C:\Program Files\Java\jdk1.8.0_101\bin

javac -d bin -cp bin src/com/bxg796/main/*.java

pause
java -cp bin com.bxg796.main.CreateDatabase
pause
