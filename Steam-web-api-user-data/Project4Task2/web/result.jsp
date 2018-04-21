<%@page import="project4task2.Entry"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%= request.getAttribute("doctype") %>
<html>
    <head>
        <title>Dashboard</title>
        <style>
        table {
            border-collapse: collapse;
            table-layout:fixed;
        }

        table, td, th {
            border: 1px solid red;
        }

        </style>
    </head>
    <body>
        <h1> Analytical Data </h1>
        <table >
            <tr><td>Most popular mobile device</td> <td><%= request.getAttribute("popularmobile")%></td></tr>
            <tr><td>Maximum number of game owned by User</td> <td><%= request.getAttribute("topgamecnt")%></td></tr>
            <tr><td>Game played for the longest time by most of people</td> <td><%= request.getAttribute("populargame")%></td></tr>
        </table>
        <h1>Logs</h1>
        <table>
        <% Entry[] entries = (Entry[]) request.getAttribute("entries"); 
           for (int i = 0; i< entries.length; i++) { %>
           <table>
                <tr><td>Device Type</td> <td><%=entries[i].getMobileType() %></td></tr>
                <tr><td>Browser</td> <td><%=entries[i].getBrowser() %></td></tr>
                <tr><td nowrap>3rd Party Request URL</td><td><%=entries[i].getUrl() %></td></tr>
                <tr><td>User Steam ID</td> <td><%=entries[i].getSteamID() %></td></tr>
                <tr><td>Game Count</td> <td><%=entries[i].getGamecount() %></td></tr>
                <tr><td>Game Name</td><td><%=entries[i].getGamename() %></td></tr>
                <tr><td>Request Received Time</td><td><%=entries[i].getTimestamp() %></td></tr>
                <tr></tr>
            </table>
            <br/>
        <% }%>
        
        <p style="color: grey; font-size: 14px;"> Author: Sisi R 2018 April</p>
    </body>
</html>

