
import { BrowserRouter } from 'react-router-dom'
import AppRoutes from './routes/AppRoutes'
import './styles/App.css'

function App() {
    return (
        <BrowserRouter>
            <AppRoutes />
        </BrowserRouter>
    )
}

export default App
