## 员工培训系统

這是一個用於公司內部員工培訓的網頁應用系統。系統允許管理員創建培訓課程及處理員工報名課程事宜，員工亦可瀏覽系統內的課程及其章節並跟踪學習進度和查看個人時間表

## 主要功能

- 用戶認證與授權(管理員/用戶)
- 課程管理與章節創建
- 顯示當前學習進度
- 課程報名與審批
- 可讀時間表

## 前置需求

要順利運行此項目 請先安裝下列必須環境變量及軟件

JAVA JDK 17 https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

Maven 3.9.9 https://maven.apache.org/download.cgi

Node.js 16+ https://nodejs.org/en

MySQL 8.0+ https://dev.mysql.com/downloads/installer/


## 安装步骤

安裝並配置好環境變量後完成以下步驟即可使用

1.打開CMD 輸入mysql -u root -p 輸入你的密碼

2.輸入 'SOURCE /your path/Staff_Training_System/init.sql' 以配置SQL

3.在後端src/main/下方添加resources文件夾並新增application.properties以設置數據庫

4.打開新的cmd 進入項目的後端部分 'cd /your path/Staff_Training_System/Back-end'

5.輸入 'mvn clean install' 後耐心等待

6.輸入 'mvn spring-boot:run' 以運行後端項目

7.打開新的cmd 進入項目的前端部分 'cd /your path/Staff_Training_System/Front-end'

8.輸入 'npm install' 以安裝前端依賴包

9.輸入 'npm run dev' 以運行前端項目

10.在前端項目輸入 'o + enter' 開啟網頁

## 配置说明

application.properties設置數據庫:

在application.properties內輸入以下字段

    spring.datasource.url=jdbc:mysql://localhost:3306/staff_training_system_database
    spring.datasource.username=root
    spring.datasource.password=你的MySQL密碼 需自行填寫
    spring.jpa.hibernate.ddl-auto=validate
    spring.jpa.show-sql=true
