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

import org.junit.jupiter.api.Test;

import fr.paris.lutece.portal.web.insert.InsertServiceSelectorJspBean;
import fr.paris.lutece.test.LuteceTestCase;
import fr.paris.lutece.test.mocks.MockHttpServletRequest;

/**
 * Tests the slideshow and workspace checks of the album insert service.
 */
public class InsertAlbumJspBeanTest extends LuteceTestCase
{
    private static final String PARAMETER_SPACE_ID = "browser_selected_space_id";
    private static final String PARAMETER_SLIDESHOW = "slideshow";
    private static final String PARAMETER_INPUT = "input";

    /**
     * The configured slideshow is offered.
     */
    @Test
    public void testGetSlideshows( )
    {
        assertTrue( InsertAlbumJspBean.getSlideshows( ).contains( "Bootstrap" ) );
    }

    /**
     * A slideshow that is not configured is never rendered: the insertion carries nothing.
     */
    @Test
    public void testUnknownSlideshowInsertsNothing( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( PARAMETER_SPACE_ID, "1" );
        request.addParameter( PARAMETER_SLIDESHOW, "../../../skin/site/page_frameset" );
        request.addParameter( PARAMETER_INPUT, "tinymce" );

        String strUrl = new InsertAlbumJspBean( ).getInsertAlbumUrl( request );

        assertTrue( strUrl.contains( "jsp/admin/insert/DoInsertIntoElement.jsp" ) );
        assertEquals( "", request.getSession( ).getAttribute( InsertServiceSelectorJspBean.SESSION_INSERT ) );
    }

    /**
     * A workspace id that is not a number shows the empty state instead of a slideshow.
     */
    @Test
    public void testInvalidSpaceShowsNoImage( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( PARAMETER_SPACE_ID, "1 OR 1=1" );
        request.addParameter( PARAMETER_SLIDESHOW, "Bootstrap" );

        String strHtml = new InsertAlbumJspBean( ).getSlideshowPreview( request );

        assertFalse( strHtml.isBlank( ) );
        assertFalse( strHtml.contains( "carousel-item" ) );
    }

    /**
     * An editor field name that is not a plain identifier is not carried into the insertion url.
     */
    @Test
    public void testUnsafeInputDropped( )
    {
        MockHttpServletRequest request = new MockHttpServletRequest( );
        request.addParameter( PARAMETER_SPACE_ID, "x" );
        request.addParameter( PARAMETER_SLIDESHOW, "Bootstrap" );
        request.addParameter( PARAMETER_INPUT, "x\"><script>alert(1)</script>" );

        String strUrl = new InsertAlbumJspBean( ).getInsertAlbumUrl( request );

        assertFalse( strUrl.contains( "script" ) );
        assertTrue( strUrl.contains( "input=&" ) );
    }
}
