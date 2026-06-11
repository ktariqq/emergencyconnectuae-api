import {useEffect, useState} from 'react';
import api from '../api/axios';
import styles from './Dashboard.module.css';

interface Incident {
    id: number;
    title: string;
    status: string;
    severity: string;
    region: string;
    createdAt: string;
}

interface Unit {
    id: number;
    name: string;
    status: string;
    type: string;
    region: string;
}

export default function Dashboard() {
    const [incidents, setIncidents] = useState<Incident[]>([]);
    const [units, setUnits] = useState<Unit[]>([]);

    useEffect(() => {
        api.get('/incidents/active').then(r => setIncidents(r.data));
        api.get('/units/available').then(r => setUnits(r.data));
    }, []);

    const severityColor = (s: string) =>
        s === 'CRITICAL' ? '#f87171' : s === 'HIGH' ? '#facc15' : '#4ade80';

    return (
        <div className={styles.page}>
            <h2 className={styles.title}>Live Operations Dashboard</h2>

            <div className={styles.grid}>
                <div className={styles.panel}>
                    <h3>🔴 Active Incidents <span className={styles.badge}>{incidents.length}</span></h3>
                    {incidents.length === 0 && <p className={styles.empty}>No active incidents.</p>}
                    {incidents.map(i => (
                        <div key={i.id} className={styles.card}>
                            <div className={styles.cardTop}>
                                <span className={styles.cardTitle}>{i.title}</span>
                                <span style={{color: severityColor(i.severity), fontSize: '0.75rem', fontWeight: 600}}>
                  {i.severity}
                </span>
                            </div>
                            <div className={styles.cardMeta}>
                                <span>{i.region}</span>
                                <span className={styles.status}>{i.status}</span>
                            </div>
                        </div>
                    ))}
                </div>

                <div className={styles.panel}>
                    <h3>🚑 Available Units <span className={styles.badge}>{units.length}</span></h3>
                    {units.length === 0 && <p className={styles.empty}>No units available.</p>}
                    {units.map(u => (
                        <div key={u.id} className={styles.card}>
                            <div className={styles.cardTop}>
                                <span className={styles.cardTitle}>{u.name}</span>
                                <span style={{color: '#4ade80', fontSize: '0.75rem', fontWeight: 600}}>
                  {u.type}
                </span>
                            </div>
                            <div className={styles.cardMeta}>
                                <span>{u.region}</span>
                                <span className={styles.statusGreen}>AVAILABLE</span>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
}