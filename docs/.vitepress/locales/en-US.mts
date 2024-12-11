import { buildSidebar } from "../builders.mts";

const lang = 'en-US';

export default {
	label: 'English',
	lang: lang,
	title: "Leawind's Third Person",
	titleTemplate: ":title | Leawind's Third Person",
	description: "Documentation of minecraft mod Leawind's Thrid Person",
	themeConfig: {
		nav: [
			{ text: '🔬Details', link: `/${lang}/Details/ItemPredicate` },
			{ text: '⚔️Compatibility', link: `/${lang}/compatibility` },
			{ text: '🕗Changelog', link: `/${lang}/changelog` },
			{ text: '💰Donate', link: `/${lang}/donate` },
			{ text: '💬Q&A', link: `https://github.com/Leawind/Third-Person/discussions/categories/q-a` },
			{
				text: "Download",
				items: [
					{ text: 'CurseForge', link: `https://www.curseforge.com/minecraft/mc-mods/leawind-third-person` },
					{ text: 'Modrinth', link: `https://modrinth.com/mod/leawind-third-person` },
					{ text: 'Github Release', link: `https://github.com/LEAWIND/Third-Person/releases` },
				],
			},
		],
		sidebar: {
			'/en-US/Details': buildSidebar(`/${lang}/Details`),
		},
		footer: {
		},
		editLink: {
			pattern: 'https://github.com/Leawind/Third-Person/edit/gh-pages/docs/:path',
			text: 'Edit this page on Github',
		},
		lastUpdated: { text: "Last updated", },
	},
};
