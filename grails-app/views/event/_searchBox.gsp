<g:form controller="event" action="search" method="GET">
    <p>Search for an event!</p>
    <g:textField name="q" maxlength="100" value="${params.q}" />&nbsp;<g:select value="${params.location}" from="${['New York','San Francisco']}" noSelection="${['':'All locations']}" name="location" />&nbsp;<g:submitButton name="searchButton" value="Search!" />
</g:form>