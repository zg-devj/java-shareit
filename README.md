# ShareIt
Сервис для шеринга вещей.

![](https://img.shields.io/github/languages/count/zg-devj/java-shareit)
![](https://img.shields.io/github/languages/code-size/zg-devj/java-shareit)

### Описание
Сервис обеспечивает пользователю возможностью поделится вещами, 
а также найти нужную вещь и взять ее в аренду на определенный 
срок.  
Если нужная вещь не найдена, то можно сделать запрос на нее. 
По запросу можно добавить новые вещи для шеринг.

### Stack
- Java 11, JPA, SpringBook, Maven, Lombok, PostgreSQL
- Тестирование: Mockito, интеграционные тесты. Тесты контроллеров, сервисов, репозиториев.   

### Fix / Update
#### 2023-6-2
- [x] Удалил из классов сущностей аннотацию @Data, вызывала переполнение стека.
#### 2023-6-3
- [x] Добавил валидацию в Dto классы запросов.
- [x] Добавил ExceptionHandler для Validation.
- [x] Тесты с несколькими проверками разделены на более мелкие.
- [x] Изменен класс вывода сообщения об ошибке.
- [x] Исправлен тест ItemRequestIntegrationTest c bidirectional отношением.