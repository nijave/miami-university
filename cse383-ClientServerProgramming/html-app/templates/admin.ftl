<#include "top.ftl">
<nav></nav>
<main>
	<h2 style="display: inline-block;">Admin</h2> - <a href="#home" id="main-page"> Home </a>
	<#if key?? >
		<style type="text/css">
			.form-field-text {
				display: inline-block;
				width: 60px;
			}
			#admin-edit textarea {
				min-width: 300px;
				width: 90%;
				min-height: 200px;
			}
		</style>
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
		<script type="text/javascript">
			var key = "${key}"; // This is populated by jetty (this page is a template)
			
			/**
			* Gets the selected story from the drop down (on the admin page)
			*/
			var getSelectedStory = function() {
				var pk = $("#story-list").find(":selected")[0].value;
				if(pk !== "0") {
					return pk;
				}
			}
			
			/**
			* Click handlers for each "page"
			*/
			var clickHandlers = function() {
				$("#main-page").click(function(){
					adminHome();
				});
			
				$("#create-story").click(function(){
					createStory();
				});
			
				$("#edit-story").click(function(obj){
					editStory(getSelectedStory());
				});
				
				$("#delete-story").click(function(){
					deleteStory(getSelectedStory());
				});
			}
			
			/**
			* Some elements are repeatedly created and destroyed
			  and need the click event handlers setup since jquery
			  only binds event handlers to elements that existed attr
			  the time the handler was specified
			* This can be avoided using the live() event handler
			  but it's not available in the version of jQuery we're
			  using
			*/
			var rebind = function() {
				$(".page-link").unbind();
				$(".page-link").click(function(e){
					e.preventDefault(); // Prevent the browser from following the link
					edit_page = e.target.innerHTML;
					editStoryPage(edit_story, edit_page);
				});
			}
			
			/**
			* Create the first page the admin sees
			* This page lists stories and allows create, edit, and delete
			*/
			var adminHome = function() {
				$(".admin-area").fadeOut("slow", function(){
					$("#admin-title").text("Story Editor"); // Change page header/title
					$("#admin-list").fadeIn();
					$.getJSON("/Story/rest/" + key + "/storylist", function(data){
						$("#story-list").find('option').remove() // Empty any existing options
						$("#story-list").append("<option value=0>Select a story</option>"); // Add the 'default' option
						for (story in data.StoryList) { // Add each story based on list from server
							$("#story-list").append("<option value=" + data.StoryList[story].pk + ">" + data.StoryList[story].title + "</option>");
						}
					});
				});
			}
			
			var createStory = function() {
				$(".admin-area").fadeOut("slow", function(){
					$("#admin-title").text("Add Story"); // Change page header/title
					$("#admin-create").fadeIn();
				});
			}
			
			/**
			* Handle the editing story page which lists
			  the pages available to edit
			*/
			var editStory = function(pk) {
				if(!pk) // Don't do anything if a primary key isn't defined (or the default option)
					return;
				$(".admin-area").fadeOut();
				$("#admin-title").text("Edit Story"); // Change header/title text
				$('#admin-edit').html(''); // Empty out any existing content from previous edits
				$.getJSON("/Story/rest/" + key + "/story/" + pk, function(data) {
					$("#admin-edit").append("<h3>" + data.title + "</h3>");
					var pageLinks = "";
					for(i = 1; i <= data.numpages; i++) { // Add a link for each page available in the story
						pageLinks += " <a href='#' class='page-link'>" + i + "</a> ";
					}
					$("#admin-edit").append("<p> Pages: " + pageLinks + "</p>"); // Add the links
					$("#admin-edit").fadeIn("slow", rebind()); // Rebind event handlers since this content changed
				});
				edit_story = pk; // This is used later by editStoryPage so it knows which page is being edited
			}
			
			/**
			* Handle editing a particular page in a story
			* This displays the correct div and populates it
			  with the correct information
			*/
			var editStoryPage = function(pk, page) {
				$(".admin-area").fadeOut();
				$("#admin-title").text("Edit Page"); // Change header/title text
				$("#admin-edit").html(""); // Empty any existing content
				$.getJSON("/Story/rest/" + key + "/story/" + pk + "/" + page, function(data){
					$("#admin-edit").append("<textarea>" + data.contents + "</textarea>"); // Story is inside here
					$("#admin-edit").append("<button id=edit-story-button>Update page</button>"); // Add submit button
					$("#edit-story-button").click(function(){ // Click handler for submit button
						$.post("/Story/rest/" + key + "/edit/" + pk + "/" + page, function(data) {
							//Dump debug info
							console.log("Update post submitted");
							console.log(data);
						}, 'json');
					});
					$("#admin-edit").fadeIn();
				});
			}
			
			/**
			* Handle deletion of a story
			*/
			var deleteStory = function(storyId) {
				if(confirm("Are you sure you want to delete the select story?")) // Use popup/confirmation box
					$.getJSON("/Story/rest/" + key + "/delete/" + storyId, function(data){});
			}
			
			/**
			* Setup the page on the initial load
			*/
			$(document).ready(function() {
				adminHome(); // default page
				clickHandlers(); //create click event handlers
			});
		</script>
		
		<h3 id="admin-title">Story Editor</h3>
		
		<div id="admin-list" class="admin-area" style="display: none;">
			<button id="create-story">Create new story</button>
			<br><br>
			<select id="story-list"></select>
			<button id="edit-story">Edit</button>
			<button id="delete-story">Delete</button>
		</div>
		
		<div id="admin-create" class="admin-area" style="display: none;">
			<label><span class="form-field-text">Title:</span><input type="text" name="story-title" defaultValue="Story Title"></label>
			<br>
			<label><span class="form-field-text">Author:</span><input type="text" name="story-author" defaultValue="Story Author"></label>
			<br>
			<button id="save-new-story" style="margin-left: 143px;">Create Story</button>
			<br>
			<p>Edit this story once it's created to add new pages</p>
		</div>
		
		<div id="admin-edit" class="admin-area" style="display: none;"></div>
	
	<#else>
		<p>You must be logged in as an administrator to view this page.</p>
	</#if>
</main>
<#include "bottom.ftl">