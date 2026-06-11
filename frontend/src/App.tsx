import {BrowserRouter, Navigate, Route, Routes} from 'react-router-dom';
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Dashboard from './pages/Dashboard';
import Incidents from './pages/Incidents';
import Units from './pages/Units';
import Assignments from './pages/Assignments';

function PrivateRoute({children}: { children: React.ReactNode }) {
    const token = localStorage.getItem('token');
    return token ? <>{children}</> : <Navigate to="/login"/>;
}

export default function App() {
    return (
        <BrowserRouter>
            <Navbar/>
            <Routes>
                <Route path="/login" element={<Login/>}/>
                <Route path="/dashboard" element={<PrivateRoute><Dashboard/></PrivateRoute>}/>
                <Route path="/incidents" element={<PrivateRoute><Incidents/></PrivateRoute>}/>
                <Route path="/units" element={<PrivateRoute><Units/></PrivateRoute>}/>
                <Route path="/assignments" element={<PrivateRoute><Assignments/></PrivateRoute>}/>
                <Route path="*" element={<Navigate to="/dashboard"/>}/>
            </Routes>
        </BrowserRouter>
    );
}