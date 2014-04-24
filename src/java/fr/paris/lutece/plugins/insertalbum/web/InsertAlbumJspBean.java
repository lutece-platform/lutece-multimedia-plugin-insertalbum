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
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.portal.web.insert.InsertServiceJspBean;
import fr.paris.lutece.portal.web.insert.InsertServiceSelectionBean;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringEscapeUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * InsertService main class
 */
public class InsertAlbumJspBean extends InsertServiceJspBean implements InsertServiceSelectionBean
{
    private static final String SLIDESHOW = "slideshow";
    private static final long serialVersionUID = -3189672631047661765L;
    private static final String TEMPLATE_SELECTOR_PAGE = "admin/plugins/insertalbum/selector.html";
    private static final String TEMPLATE_PRESENTATION = "admin/plugins/insertalbum/presentation.html";

    // template for all admin pages
    private static final String TEMPLATE_CHOOSE_DOCUMENT = "admin/plugins/insertalbum/choose_workspace.html";
    private static final String MARK_SPACES_BROWSER = "spaces_browser";
    private static final String PARAMETER_INPUT = "input";
    private static final String IMAGES_LIST = "images_list";
    private static final String BASE_URL = "baseUrl";
    private static final String SLIDESHOWLIST = "slideshow_list";
    private static final String TEMPLATE_PREVIEW_SLIDESHOW = "admin/plugins/insertalbum/choose_slideshow.html";
    private AdminUser _user;
    private String _input;
    private String PROPERTY_SLIDESHOW_LIST = "insertalbum.slideshows";

    private void init( HttpServletRequest request )
    {
        _user = AdminUserService.getAdminUser( request );
        _input = request.getParameter( PARAMETER_INPUT );
    }

    public String getInsertServiceSelectorUI( HttpServletRequest request )
    {
        init( request );

        Locale locale = AdminUserService.getLocale( request );
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( PARAMETER_INPUT, _input );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PRESENTATION, locale, model );

        return template.getHtml(  );
    }

    public String getSpaceBrowser( HttpServletRequest request )
    {
        init( request );

        Map<String, Object> model = new HashMap<String, Object>(  );
        String strSpaceId = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

        if ( ( strSpaceId != null ) && !strSpaceId.equals( "" ) )
        {
            return getSelectSlideshow( request );
        }

        Locale locale = AdminUserService.getLocale( request );
        // Spaces browser
        model.put( MARK_SPACES_BROWSER,
            DocumentSpacesService.getInstance(  ).getSpacesBrowser( request, _user, locale, true, true ) );

        String strBaseUrl = AppPathService.getBaseUrl( request );
        model.put( BASE_URL, strBaseUrl );
        model.put( PARAMETER_INPUT, _input );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_CHOOSE_DOCUMENT, locale, model );

        return template.getHtml(  );
    }

    /**
     * Give the user the choose of the workspace
     *
     * @param request The Http Request
     * @return The html form.
     */
    public String getSelectSlideshow( HttpServletRequest request )
    {
        String slideshowListInProperties = AppPropertiesService.getProperty( PROPERTY_SLIDESHOW_LIST );

        List<String> slideshowList = Arrays.asList( slideshowListInProperties.split( "," ) );

        String strIdWorksapce = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( PARAMETER_INPUT, _input );
        model.put( SLIDESHOWLIST, slideshowList );

        String strBaseUrl = AppPathService.getBaseUrl( request );
        model.put( BASE_URL, strBaseUrl );
        model.put( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID, strIdWorksapce );

        Locale locale = AdminUserService.getLocale( request );
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_SELECTOR_PAGE, locale, model );

        return template.getHtml(  );
    }

    public String doInsertLink( HttpServletRequest request )
    {
        //get the name of the html tag to return to the WYNGZIG editor
        String strInput = request.getParameter( PARAMETER_INPUT );

        try
        {
            HtmlTemplate template = getSlideshowTemplate( request );

            return insertUrlWithoutEscape( request, strInput, template.getHtml(  ) );
        }
        catch ( NoImageInWorkspaceException e )
        {
            return insertUrlWithoutEscape( request, strInput, "" );
        }
    }

    public String doGetSlideshow( HttpServletRequest request )
    {
        try
        {
            HtmlTemplate templateSlideshow = getSlideshowTemplate( request );

            Map<String, Object> model = new HashMap<String, Object>(  );
            Locale locale = AdminUserService.getLocale( request );
            model.put( SLIDESHOW, templateSlideshow.getHtml(  ) );

            String strBaseUrl = AppPathService.getBaseUrl( request );
            model.put( BASE_URL, strBaseUrl );
            model.put( PARAMETER_INPUT, _input );

            HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_PREVIEW_SLIDESHOW, locale, model );

            return template.getHtml(  );
        }
        catch ( NoImageInWorkspaceException e )
        {
            return "no image in workspace";
        }
    }

    private HtmlTemplate getSlideshowTemplate( HttpServletRequest request )
        throws NoImageInWorkspaceException
    {
        //get the workspace of witch we want the images
        String strIdWorksapce = request.getParameter( DocumentSpacesService.PARAMETER_BROWSER_SELECTED_SPACE_ID );
        int nIdWorksapce = Integer.parseInt( strIdWorksapce );

        //get the name of the template (for the slideshow) to use
        String strSlideshow = request.getParameter( SLIDESHOW );

        //Collection<Image> imagesList = InsertAlbumHome.findImagesListOfWorkspace( nIdWorksapce, _plugin );
        List<Document> documentsList = DocumentHome.findBySpaceKey( nIdWorksapce );

        for ( Document doc : documentsList )
        {
            doc.setComment( StringEscapeUtils.escapeHtml( doc.getSummary(  ) ) );
            doc.setComment( doc.getComment(  ).replaceAll( "'", "&#146;" ) );
        }

        HtmlTemplate template = null;

        String strBaseUrl = AppPathService.getBaseUrl( request );
        Locale locale = AdminUserService.getLocale( request );

        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( IMAGES_LIST, documentsList );
        model.put( BASE_URL, strBaseUrl );

        if ( ( documentsList != null ) && ( documentsList.size(  ) > 0 ) && ( strSlideshow != null ) )
        {
            template = AppTemplateService.getTemplate( "admin/plugins/insertalbum/slideshow/" + strSlideshow + ".html",
                    locale, model );
        }
        else
        {
            throw new NoImageInWorkspaceException(  );
        }

        return template;
    }
}
