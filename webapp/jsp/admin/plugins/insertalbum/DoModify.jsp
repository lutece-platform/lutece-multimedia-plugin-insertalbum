<%@ page errorPage="../../ErrorPage.jsp" %>

<jsp:useBean id="insertajax" scope="session" class="fr.paris.lutece.plugins.insertajax.web.InsertAjaxJspBean" />

<%
    insertajax.init( request, insertajax.RIGHT_MANAGE );
    response.sendRedirect( insertajax.doModify( request ) );
%>
