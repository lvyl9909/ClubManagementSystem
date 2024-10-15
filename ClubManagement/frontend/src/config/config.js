export default [
    {
        path: '/home',
        name: 'home',
        label: 'Home',
        icon: 'HomeOutlined',
        url: '/home/index',
        roles: ['ROLE_USER'], // 所有角色都可以看到
    },
    {
        path: '/club',
        name: 'club',
        label: 'Club',
        icon: 'ShopOutlined',
        url: '/club/index',
        roles: ['ROLE_USER'], // 仅管理员可以看到
    },
    {
        path: '/event',
        name: 'event',
        label: 'Event',
        icon: 'UserOutlined',
        url: '/event/index',
        roles: ['ROLE_USER'], // 仅用户可以看到
    },
    {
        path: '/other',
        label: 'Other',
        icon: 'SettingOutlined',
        roles: ['ROLE_USER'], // 所有角色都可以看到
        children: [
            {
                path: '/other/pageOne',
                name: 'page1',
                label: 'Page1',
                icon: 'SettingOutlined',
                roles: ['ROLE_USER'], // 仅管理员可以看到
            },
            {
                path: '/other/pageTwo',
                name: 'page2',
                label: 'Page2',
                icon: 'SettingOutlined',
                roles: ['ROLE_USER'], // 仅用户可以看到
            }
        ]
    },
    {
        path: '/view_funding',
        name: 'view_funding',
        label: 'View',
        icon: 'SettingOutlined',
        url: '/view_funding/index',
        roles: ['ROLE_ADMIN'], // 仅管理员可以看到
    },
    {
        path: 'logout',
        name: 'logout',
        label: 'Logout',
        icon: 'LogoutOutlined',
        roles: ['ROLE_ADMIN', 'ROLE_USER'], // 所有角色都可以看到
    }
];
