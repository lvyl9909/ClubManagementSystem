export default  [
    {
        path: '/home',
        name: 'home',
        label: 'Home',
        icon: 'HomeOutlined',
        url: '/home/index'
    },
    {
        path: '/club',
        name: 'club',
        label: 'Club',
        icon: 'ShopOutlined',
        url: '/club/index'
    },
    {
        path: '/event',
        name: 'event',
        label: 'Event',
        icon: 'UserOutlined',
        url: '/event/index'
    },
    {
        path: '/other',
        label: 'Other',
        icon: 'SettingOutlined',
        children: [
            {
                path: '/other/pageOne',
                name: 'page1',
                label: 'Page1',
                icon: 'SettingOutlined'
            },
            {
                path: '/other/pageTwo',
                name: 'page2',
                label: 'Page2',
                icon: 'SettingOutlined'
            }
        ]
    }
]