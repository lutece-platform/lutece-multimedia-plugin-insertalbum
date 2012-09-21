<%@ page errorPage="../../ErrorPage.jsp" %>
<jsp:include page="../../insert/InsertServiceHeader.jsp" />

<jsp:useBean id="albumajax" scope="session" class="fr.paris.lutece.plugins.insertalbum.web.InsertAlbumJspBean" />
<%= albumajax.getSpaceBrowser( request ) %>

</body>
</html>


