前置需求
----------------------------------------------------------------------------------------------------------------
要順利運行此項目 請先安裝下列必須環境變量及軟件

JAVA JDK 17 https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

Maven 3.9.9 https://maven.apache.org/download.cgi

Node.js https://nodejs.org/en

MySQL https://dev.mysql.com/downloads/installer/


使用說明:
----------------------------------------------------------------------------------------------------------------
安裝並配置好環境變量後完成以下步驟即可使用

1.打開CMD 輸入mysql -u root -p 輸入你的密碼

2.輸入 SOURCES /your path/Staff_Training_System/init.sql以配置SQL

3.使用cmd 進入項目的後端部分/Back-end

4.輸入 mvn clean install 後耐心等待

5.輸入 mvn spring-boot:run 以運行後端項目

6.打開新的cmd 進入項目的前端部分 /Front-end

7.輸入 npm run dev 以開啟前端伺服器

8.完成

----------------------------------------------------------------------------------------------------------------



