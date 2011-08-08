<g:form controller="event" action="search" method="GET">
    <p>Search for an event!</p>
    <g:textField name="q" maxlength="100" value="${params.q}" />&nbsp;<g:select value="${params.location}" from="${['New York','San Francisco']}" noSelection="${['':'All locations']}" name="location" />&nbsp;<g:submitButton name="searchButton" value="Search!" />&nbsp;<g:hiddenField id="useES" name="useES" value="true" disabled="disabled" /><g:submitButton name="searchButton" value="Search using ElasticSearch!" onclick="document.getElementById('useES').disabled=false;" />
</g:form>