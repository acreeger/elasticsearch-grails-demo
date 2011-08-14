
<%@ page import="org.cafeCentro.Event" %>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
        <meta name="layout" content="main" />
        <g:set var="entityName" value="${message(code: 'event.label', default: 'Event')}" />
        <title><g:message code="default.list.label" args="[entityName]" /></title>
    </head>
    <body>
        <div class="nav">
            <span class="menuButton"><a class="home" href="${createLink(uri: '/')}"><g:message code="default.home.label"/></a></span>
            <span class="menuButton"><g:link class="create" action="create"><g:message code="default.new.label" args="[entityName]" /></g:link></span>
        </div>
        <div class="body">
            <h1><g:message code="default.list.label" args="[entityName]" /></h1>
            <g:if test="${flash.message}">
            <div class="message">${flash.message}</div>
            </g:if>
            <g:render template="searchBox" />
            <g:if test="${params.q || params.location}">
                <p>Displaying results ${start} to ${stop} of ${eventInstanceTotal} ${eventInstanceTotal == 1 ? 'result' : 'results'}. Search took ${searchTime/1000}s.</p>
            </g:if>
            <div class="list">
                <table>
                    <thead>
                        <tr>
                        
                            <g:sortableColumn property="name" title="${message(code: 'event.name.label', default: 'Name')}" />
                        
                            <g:sortableColumn property="date" title="${message(code: 'event.date.label', default: 'Date')}" />
                        
                            <th><g:message code="event.venue.label" default="Venue" /></th>
                        
                        </tr>
                    </thead>
                    <tbody>
                    <g:each in="${eventInstanceList}" status="i" var="eventInstance">
                        <tr class="${(i % 2) == 0 ? 'odd' : 'even'}">
                        
                            <td><g:link action="show" id="${eventInstance.id}">${fieldValue(bean: eventInstance, field: "name")}</g:link></td>
                        
                            <td><g:formatDate date="${eventInstance.date}" /></td>
                        
                            <td>${fieldValue(bean: eventInstance, field: "venue")}</td>
                        
                        </tr>
                    </g:each>
                    </tbody>
                </table>
            </div>
            <div class="paginateButtons">
                <g:paginate total="${eventInstanceTotal}" params="${paginationParams ?: [:]}" />
            </div>
        </div>
    </body>
</html>
