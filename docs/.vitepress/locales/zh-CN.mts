import { buildSidebar } from "../builders.mts";

const lang = 'zh-CN';

export default {
	label: '简体中文',
	lang: lang,
	title: "Leawind的第三人称",
	titleTemplate: ":title | Leawind的第三人称",
	description: "一个Minecraft模组 Leawind的第三人称 的文档",
	themeConfig: {
		nav: [
			{ text: '🔬详细', link: `/${lang}/Details/` },
			{ text: '⚔️兼容性', link: `/${lang}/compatibility` },
			{ text: '🕗更新日志', link: `/${lang}/changelog` },
			{ text: '💰捐赠', link: `/${lang}/donate` },
			{ text: '💬疑问', link: `https://github.com/Leawind/Third-Person/discussions/categories/q-a` },
			{
				text: "下载",
				items: [
					{ text: 'CurseForge', link: `https://www.curseforge.com/minecraft/mc-mods/leawind-third-person` },
					{ text: 'Modrinth', link: `https://modrinth.com/mod/leawind-third-person` },
					{ text: 'Github Release', link: `https://github.com/LEAWIND/Third-Person/releases` },
				],
			},
		],
		sidebar: {
			'/zh-CN/Details': buildSidebar(`/${lang}/Details`),
		},
		footer: {
		},
		editLink: {
			pattern: 'https://github.com/Leawind/Third-Person/edit/gh-pages/docs/:path',
			text: '在 Github 上编辑此页',
		},
		lastUpdated: { text: "上次更新", },
	},
};
