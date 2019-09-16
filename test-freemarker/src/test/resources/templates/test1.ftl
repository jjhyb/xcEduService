<!DOCTYPE html>
<html>
<head>
    <meta charset="utf‐8">
    <title>Hello World!</title>
</head>
<body>
Hello ${name}!
<br>
遍历数据模型中的list的学生信息（数据模型中的名称为stus）
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>金额</td>
        <td>生日</td>
    </tr>
    <#if stus??>
        <#list stus as stu>
            <tr>
                <td>${stu_index+1}</td>
                <td <#if stu.name=='小明'>style="background:red;"</#if>>${stu.name}</td>
                <td>${stu.age}</td>
                <td <#if (stu.money > 300)>style="background:red;"</#if>>${stu.money}</td>
                <td>${stu.birthday?date}</td>
            </tr>
        </#list>
        <br>
        学生的数量：${stus?size}
    </#if>
</table>

<br>
遍历数据模型中的stuMap（stuMap数据），
第一种方法：在中括号中填写map的key
第二种方法：在map后面直接加"点key"
<br>
姓名：${(stuMap['stu1'].name)!''}<br>
年龄：${(stuMap['stu1'].age)!''}<br>
姓名：${(stuMap.stu1.name)!''}<br>
年龄：${(stuMap.stu1.age)!''}<br>
遍历map中的key，stuMap?keys就是key的一个列表(是一个list)
<br>
<#list stuMap?keys as key>
    姓名：${stuMap[key].name}<br>
    年龄：${stuMap[key].age}<br>
</#list>
<br>
${point?c}
<br>
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank} 账号：${data.account}
</body>
</html>