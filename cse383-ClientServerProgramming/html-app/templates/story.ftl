<#include "top.ftl">
<nav>
	<a href="/Story/select">Home</a>
	<br>
	<#if prevLink??>
		<a href="${prevLink}">Previous Page</a>
		<br>
	</#if>
	<#if nextLink??>
		<a href="${nextLink}">Next Page</a>
	</#if>
</nav>
<main>
	${text}
</main>
<#include "bottom.ftl">