function loadcssfile(filename) 
{
	var fileref=document.createElement("link");
	fileref.setAttribute("rel", "stylesheet");
	fileref.setAttribute("type", "text/css");
	fileref.setAttribute("href", filename);
	document.getElementsByTagName("head")[0].appendChild(fileref);
}

function loadScriptForGalleryView2() 
{
	//TODO this path is set up directly in the galleryview js file. Which is wrong.
	var theme_path = "js/plugins/plugin-insertalbum/jquery-galleryview-2.0/themes/";
	$.ajaxSetup({async: false});
	loadcssfile("js/plugins/plugin-insertalbum/jquery-galleryview-2.0/galleryview.css");
	$.getScript("js/plugins/plugin-insertalbum/jquery.easing.1.3.js");
	$.getScript("js/plugins/plugin-insertalbum/jquery.timers-1.1.2.js");
	$.getScript("js/plugins/plugin-insertalbum/jquery-galleryview-2.0/jquery.galleryview-2.0.js");
	$.ajaxSetup({async: true});
}

function loadScriptForGalleryView1() 
{
	$.ajaxSetup({async: false});
	$.getScript("js/plugins/plugin-insertalbum/jquery.easing.1.3.js");
	$.getScript("js/plugins/plugin-insertalbum/jquery-galleryview-1.1/jquery.galleryview-1.1.js");
	$.getScript("js/plugins/plugin-insertalbum/jquery.timers-1.1.2.js");
	$.ajaxSetup({async: true});
}

/**********************************************************************************************/

$(document).ready(function () 
{
	if( $('.ppy').length != 0 )
	{
		$.ajaxSetup({async: false});
		loadcssfile("css/plugins/plugin-insertalbum/jquery.popeye.css");
		loadcssfile("css/plugins/plugin-insertalbum/jquery.popeye.style.css");
		$.getScript("js/plugins/plugin-insertalbum/jquery.popeye-2.0.4.min.js");
		$.ajaxSetup({async: true});
		$('.ppy').popeye();
	}

	/* http://spaceforaname.com/galleryview */
	if( document.getElementById("albumLuteceGalleryView") != null )
	{
		loadScriptForGalleryView2();
		$('#albumLuteceGalleryView').galleryView({
			panel_width: 400,
			panel_height: 300,
			frame_width: 40,
			frame_height: 40,
			filmstrip_position:'left',
		    background_color: 'transparent',
			frame_opacity: 0.2,
			frame_gap: 20,
			pointer_size: 0,
			frame_scale: 'crop',
			nav_theme: 'dark'
		});
	}

	/* http://spaceforaname.com/polaroid.html */
	if( document.getElementById("albumLuteceGalleryViewPolaroid") != null )
	{
		loadScriptForGalleryView1();
		$('#albumLuteceGalleryViewPolaroid').galleryView({
			panel_width: 469,
		    panel_height: 452,
		    frame_width: 114,
		    frame_height: 110,
		    transition_speed: 1200,
		    background_color: 'transparent',
		    border: 'none',
		    easing: 'easeOutBounce',
		    nav_theme: 'dark'
		});
	}

	/* http://spaceforaname.com/filmstrip.html */
	if( document.getElementById("albumLuteceGalleryViewFilmStrip") != null )
	{
		loadScriptForGalleryView1();
		$('#albumLuteceGalleryViewFilmStrip').galleryView({
			filmstrip_size: 4,
			frame_width: 100,
			frame_height: 100,
			background_color: 'transparent',
			nav_theme: 'dark',
			border: 'none',
			show_captions:true,
			caption_text_color: 'black'
		});
	}

	/* http://spaceforaname.com/panels.html */
	if( document.getElementById("albumLuteceGalleryViewPanelOnly") != null )
	{
		loadScriptForGalleryView1();
		$('#albumLuteceGalleryViewPanelOnly').galleryView({
			panel_width: 800,
			panel_height: 300,
			transition_speed: 1500,
			transition_interval: 5000,
			nav_theme: 'dark',
			border: '1px solid #666666',
			pause_on_hover: true
		});
	}
});
