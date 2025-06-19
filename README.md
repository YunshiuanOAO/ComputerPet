# ComputerPet 桌面寵物應用程式

這是一個基於 Java Swing 開發的桌面寵物應用程式。

## 專案設置與運行

### 1. 克隆專案

首先，使用 Git 克隆本專案到您的本地機器：

```bash
git clone https://github.com/YunshiuanOAO/ComputerPet.git
cd ComputerPet
```

### 2. 編譯專案

本專案使用 Apache Maven 進行建構。請確保您已[安裝 Maven](https://maven.apache.org/install.html)。您可以使用以下命令來編譯專案：

```bash
mvn clean install
```

這個命令會清理舊的編譯檔案，下載所有必要的依賴，並編譯專案，最終生成一個可執行的 JAR 檔案，位於 `target` 目錄下。

### 3. 運行應用程式

有兩種主要方式可以運行此應用程式：

#### 方式一：使用 Maven 運行 (開發階段推薦)

這種方式會檢查程式碼是否有更新並進行編譯（如果需要），然後直接執行應用程式。對於開發和測試來說非常方便：

```bash
mvn exec:java
```

#### 方式二：運行已編譯的 JAR 檔案 (發布或快速啟動)

一旦您完成了編譯（透過 `mvn clean install`），您可以在 `target` 目錄中找到生成的 JAR 檔案（例如 `ComputerPet-1.0-SNAPSHOT.jar`）。您可以直接運行這個 JAR 檔案，這不需要 Maven 環境，只需要有 Java Runtime Environment (JRE)：

```bash
java -jar target/ComputerPet-1.0-SNAPSHOT.jar
```

請根據您的需求選擇合適的運行方式。 