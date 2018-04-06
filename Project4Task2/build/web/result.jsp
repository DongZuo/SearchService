<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Web Analytics</title>
    </head>
    <body>
        <!-- I learned how to use jsp table from this reference -->
        <!-- https://stackoverflow.com/questions/13759149/how-to-display-values-in-table-in-jsp?utm_medium=organic&utm_source=google_rich_qa&utm_campaign=google_rich_qa -->
        
        <h3>Analytics Statistics</h3>
        <table>
            <tr>
                <th> Top 3 Search Term </th>
            </tr>
            <tr>
                <c:forEach items="${searchList}" var="search" varStatus="status">
                    <td>"${search.key}"</td>
                </c:forEach>
            </tr>
            <tr>
                <th> Top 3 Titles</th>
            </tr>
            <tr>
                <c:forEach items="${titleList}" var="title" varStatus="status">
                    <td>"${title.key}"</td>
                </c:forEach>
            </tr>
            <tr>
                <th> Top 3 Authors</th>
            </tr>
            <tr>
                <c:forEach items="${authorList}" var="author" varStatus="status">
                    <td>"${author.key}"</td>
                </c:forEach>
            </tr>
            <tr>
                <th> Top 3 Mobile Devices</th>
            </tr>
            <tr>
                <c:forEach items="${deviceList}" var="device" varStatus="status">
                    <td>"${device.key}"</td>
                </c:forEach>
            </tr>
        </table>        
        
        <br><br><br>
        <h3>Full Logs</h3>
        <table>
            <tr>
                <th>visited time</th>
                <th>device type</th>
                <th>search term</th>
                <th>url</th>
                <th>reply from API</th>
                <th>book title</th>
                <th>book author</th>
                <th>status code</th>

            </tr>
            <c:forEach items="${logList}" var="log" varStatus="status">
                <tr>
                    <td>${log.log1} </td>
                    <td>${log.log2} </td>
                    <td>${log.log3} </td>
                    <td>${log.log4} </td>
                    <td>${log.log5} </td>
                    <td>${log.log6} </td>
                    <td>${log.log7} </td>
                    <td>${log.log8} </td>

                </tr>
            </c:forEach>
        </table>
        
        
    </body>
</html>

