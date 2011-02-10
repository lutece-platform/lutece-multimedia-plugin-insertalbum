<%@ page errorPage="ErrorPage.jsp" %>
<jsp:useBean id="albumajax" scope="session" class="fr.paris.lutece.plugins.insertalbum.web.InsertAlbumJspBean" />
<%= albumajax.getSpaceBrowser( request ) %>