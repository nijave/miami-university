<#include "top.ftl">
<nav>
<#if isAdmin??>
	<a href="/Story/admin">Admin</a>
</#if>
</nav>
<main>
	<h2>Stories</h2>
	<ul>
	<#list stories as item>
		<li><a href="/Story/read/${item}">${item}</a></li>
	</#list>
	</ul>
	<#if !username?? || !email??>
		<p>
			<form action="/Story/select" method="post">
				<label>Username:<br><input type="text" name="username"></label>
				<br>
				<label>Password:<br><input type="password" name="password"></label>
				<br>
				<input type="submit">
			</form>
		</p>
	</#if>
</main>
<#include "bottom.ftl">