Option Explicit

Dim fso, root, shell, java, node, jar
Set fso = CreateObject("Scripting.FileSystemObject")
Set shell = CreateObject("WScript.Shell")
root = fso.GetParentFolderName(WScript.ScriptFullName)
java = "D:\jdk-8u481-windows-x64\jdk1.8.0_481\bin\java.exe"
node = "D:\nvm\nodejs\node.exe"
jar = root & "\backend\target\library-api-1.0.0.jar"

shell.CurrentDirectory = root
shell.Run Chr(34) & java & Chr(34) & " -jar " & Chr(34) & jar & Chr(34), 0, False
shell.Run Chr(34) & node & Chr(34) & " " & Chr(34) & root & "\node_modules\vite\bin\vite.js" & Chr(34) & " --host 0.0.0.0", 0, False
