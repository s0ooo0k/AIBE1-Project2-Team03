# TEAMO !
> AIBE1 TEAM03 호식이세마리 | 팀원 준비 완료! 팀워크가 필요할 땐, Teamo



### ✅ application-dev.yml
```yml
server:
  port: 8080
spring:
  datasource:
    driver-class-name: com.mysql.cj.jdbc.Driver
    url: ${DB-URL}
    username: ${DB-NAME}
    password: ${DB-PW}
  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        show_sql: true
springdoc:
  swagger-ui:
    enabled: true
  api-docs:
    enabled: true
```
