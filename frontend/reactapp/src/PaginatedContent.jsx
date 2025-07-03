import { useState } from 'react'
import HomePage from './pages/HomePage'
import ShowAllStaticPage from './pages/ShowAllStaticPage'
import ShowAllDynamicPage from './pages/ShowAllDynamicPage'


function PaginatedContent() {
  const [store, setStore] = useState({
    staticArticles: [],
    dynamicArticles: [],
  })

  const [isForced, setIsForced] = useState(false)

  const [home, showAllStatic, showAllDynamic] = ["home", "showAllStatic", "showAllDynamic"]
  const [page, setPage] = useState(home)

  switch (page) {
    case home:
        return <HomePage store={store} setPage={setPage} isForced={isForced} setIsForced={setIsForced}  />
    case showAllStatic:
      return <ShowAllStaticPage store={store} setStore={setStore} setPage={setPage}  />

    case showAllDynamic:
      return <ShowAllDynamicPage store={store} setStore={setStore} setPage={setPage}  />
    default:
      return <Error text={"Page Error: Unknown page"} setPage={setPage} />
  }
}

export default PaginatedContent
