<!doctype html>
<html>
<head>
	<title>Story program<#if pageTitle??> | ${pageTitle}</#if></title>
	<style type="text/css">
		html, body {
			margin: 0;
			padding: 0;
			width: 100%;
			height: 100%;
		}

		#container {
			/*width: 900px;
			margin: 6px auto;
			display: flex;*/
			flex-flow: row wrap;
		}

		header, section {
			display: flex;
			flex: 1 100%;
		}

		section {
			margin: 6px 0 10px 0;
			padding: 0;
		}

		header {
			flex-direction: column;
			padding: 12px 0 12px 0;
			border-bottom: 10px solid orange;
		}
			#title {
				display: flex;
				margin: 0;
				padding: 20px;
				font-size: 3em;
			}
			
			#userInfo {
				margin-left: 22px;
			}

		nav {
			min-width: 170px;
		}

		main {
			margin-left: 34px;
		}

		#bottom {
			justify-content: space-between;
		}
	</style>
</head>
<body>
<div id="container">
	<section>
		<header>
			<h1 id="title"> Nick's Story Reader </h1>
			<span id="userInfo">
				<#if username??><b>Username:</b> ${username}</#if>
				&nbsp;
				<#if email??><b>Email:</b> ${email}</#if>
			</span>
		</header>
	</section>
	<section>
