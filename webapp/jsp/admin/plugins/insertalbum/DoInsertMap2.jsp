<%@ page errorPage="ErrorPage.jsp" %>
<jsp:useBean id="albumajax" scope="session" class="fr.paris.lutece.plugins.insertalbum.web.InsertAlbumJspBean" />
<% 
     response.sendRedirect( albumajax.doInsertLink( request ) );
%>
