/*
 * Copyright (c) 2002-2014, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.insertalbum.web;

import fr.paris.lutece.plugins.document.business.Document;
import fr.paris.lutece.plugins.document.business.DocumentHome;
import fr.paris.lutece.plugins.document.service.spaces.DocumentSpacesService;
import fr.paris.lutece.plugins.insertalbum.service.NoImageInWorkspaceException;
import fr.paris.lutece.portal.business.user.AdminUser;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.security.ISecurityTokenService;
import fr.paris.lutece.portal.service.security.SecurityTokenHandler;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import jakarta.enterprise.context.RequestScoped;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.servlet.http.HttpServletRequest;

/**
 * Insert service that inserts into a rich text editor a slideshow of the documents of a workspace.
 */
@RequestScoped
public class InsertAlbumJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    private static final long serialVersionUID = -3189672631047661765L;

    private static final String REGEX_ID = "^[\\d]+$";
    private static final String REGEX_INPUT = "^[\\w-]+$";
    private static final String DEFAULT_SLIDESHOWS = "Bootstrap";

    private static final String PROPERTY_SLIDESHOW_LIST = "insertalbum.slideshows";

    private static final String PARAMETER_INPUT = "input";
    private static final String PARAMETER_SLIDESHOW = "slideshow";

    private static final String MARK_INPUT = "input";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String MARK_SLIDESHOW_LIST = "slideshow_list";
    private static final String MARK_IMAGES_LIST = "images_list";
    private static final String MARK_BASE_URL = "baseUrl";

    private static final String TEMPLATE_PRESENTATION = "admin/plugins/insertalbum/presentation.html";
    private static final String TEMPLATE_CHOOSE_WORKSPACE = "admin/plugins/insertalbum/choose_workspace.html";
    private static final String TEMPLATE_SELECTOR = "admin/plugins/insertalbum/selector.html";
    private static final String TEMPLATE_PREVIEW_EMPTY = "admin/plugins/insertalbum/preview_empty.html";
    private static final String TEMPLATE_SLIDESHOW = "admin/plugins/insertalbum/slideshow/%s.html";

    private DocumentSpacesService _spacesService = CDI.current( ).select( DocumentSpacesService.class ).get( );
    private ISecurityTokenService _securityTokenService = CDI.current( ).select( ISecurityTokenService.class ).get( );

    /**
     * Returns the page that opens the album selector of the plugin.
     *
     * @param request
     *            the HTTP request
     * @return the HTML of the page
     */
    @Override
    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_INPUT, getInput( request ) );

        return AppTemplateService.getTemplate( TEMPLATE_PRESENTATION, AdminUserService.getLocale( request ), model ).getHtml( );
    }

    /**
     * Returns the workspace browser, or the slideshow selector once a workspace is selected.
     *
     * @param request
     *            the HTTP request
     * @param strInsertAction
     *            the action of the controller that inserts the album
     * @return the HTML of the page
     */
    public String getChooseAlbum( HttpServletRequest request, String strInsertAction )
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        Locale locale = AdminUserService.getLocale( request );
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_INPUT, getInput( request ) );
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

        if ( ( strSpaceId != null ) && strSpaceId.matches( REGEX_ID ) )
        {
            model.put( MARK_SLIDESHOW_LIST, getSlideshows( ) );
            model.put( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );
            model.put( SecurityTokenHandler.MARK_CSRF_TOKEN, _securityTokenService.getToken( request, strInsertAction ) );

            return AppTemplateService.getTemplate( TEMPLATE_SELECTOR, locale, model ).getHtml( );
        }

        model.put( MARK_SPACES_BROWSER, _spacesService.getSpacesBrowser( request, user, locale, true, true ) );

        return AppTemplateService.getTemplate( TEMPLATE_CHOOSE_WORKSPACE, locale, model ).getHtml( );
    }

    /**
     * Returns the preview of the selected slideshow.
     *
     * @param request
     *            the HTTP request
     * @return the HTML of the slideshow, or an empty state when no slideshow can be built
     */
    public String getSlideshowPreview( HttpServletRequest request )
    {
        try
        {
            return getSlideshowHtml( request );
        }
        catch( NoImageInWorkspaceException e )
        {
            return AppTemplateService.getTemplate( TEMPLATE_PREVIEW_EMPTY, AdminUserService.getLocale( request ), new HashMap<>( ) ).getHtml( );
        }
    }

    /**
     * Builds the url that inserts the selected slideshow into the calling editor field.
     *
     * @param request
     *            the HTTP request
     * @return the insertion url
     */
    public String getInsertAlbumUrl( HttpServletRequest request )
    {
        String strInsert;

        try
        {
            strInsert = getSlideshowHtml( request );
        }
        catch( NoImageInWorkspaceException e )
        {
            strInsert = "";
        }

        return insertUrl( request, getInput( request ), StringEscapeUtils.escapeEcmaScript( strInsert ) );
    }

    /**
     * Returns the slideshow templates configured for the plugin.
     *
     * @return the names of the slideshow templates
     */
    public static List<String> getSlideshows( )
    {
        return Arrays.asList( AppPropertiesService.getProperty( PROPERTY_SLIDESHOW_LIST, DEFAULT_SLIDESHOWS ).split( "," ) );
    }

    /**
     * Returns the name of the editor field to insert into, when it is a plain identifier.
     *
     * @param request
     *            the HTTP request
     * @return the field name, or an empty string
     */
    private static String getInput( HttpServletRequest request )
    {
        String strInput = request.getParameter( PARAMETER_INPUT );

        return ( ( strInput != null ) && strInput.matches( REGEX_INPUT ) ) ? strInput : StringUtils.EMPTY;
    }

    /**
     * Renders the selected slideshow with the documents of the selected workspace.
     *
     * @param request
     *            the HTTP request
     * @return the HTML of the slideshow
     * @throws NoImageInWorkspaceException
     *             if the workspace is unknown, not viewable by the user or holds no document, or the slideshow is unknown
     */
    private String getSlideshowHtml( HttpServletRequest request ) throws NoImageInWorkspaceException
    {
        AdminUser user = AdminUserService.getAdminUser( request );
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        String strSlideshow = request.getParameter( PARAMETER_SLIDESHOW );

        if ( ( strSpaceId == null ) || !strSpaceId.matches( REGEX_ID ) || !getSlideshows( ).contains( strSlideshow ) )
        {
            throw new NoImageInWorkspaceException( );
        }

        int nIdSpace = Integer.parseInt( strSpaceId );

        if ( !_spacesService.isAuthorizedViewByRole( nIdSpace, user ) || !_spacesService.isAuthorizedViewByWorkgroup( nIdSpace, user ) )
        {
            throw new NoImageInWorkspaceException( );
        }

        List<Document> listDocuments = DocumentHome.findBySpaceKey( nIdSpace );

        if ( listDocuments.isEmpty( ) )
        {
            throw new NoImageInWorkspaceException( );
        }

        for ( Document document : listDocuments )
        {
            document.setComment( StringEscapeUtils.escapeHtml4( StringUtils.defaultString( document.getSummary( ) ) ).replace( "'", "&#39;" ) );
        }

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_IMAGES_LIST, listDocuments );
        model.put( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID, strSpaceId );
        model.put( MARK_BASE_URL, AppPathService.getBaseUrl( request ) );

        return AppTemplateService.getTemplate( String.format( TEMPLATE_SLIDESHOW, strSlideshow ), AdminUserService.getLocale( request ), model ).getHtml( );
    }
}
