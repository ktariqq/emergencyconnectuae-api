import {useEffect, useState} from 'react';
import api from '../api/axios';
import styles from './Units.module.css';

export default function Units() {
    const [units, setUnits] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [form, setForm] = useState({name: '', type: 'AMBULANCE', region: ''});

    const fetchUnits = () => {
        api.get(`/units?page=${page}&size=10`).then(r => {
            setUnits(r.data.content);
            setTotalPages(r.data.totalPages);
        });
    };

    useEffect(() => {
        fetchUnits();
    }, [page]);

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        await api.post('/units', form);
        fetchUnits();
        setForm({name: '', type: 'AMBULANCE', region: ''});
    };

    const updateStatus = async (id: number, status: string) => {
        await api.put(`/units/${id}/status?status=${status}`);
        fetchUnits();
    };

    const statusColor = (s: string) =>
        s === 'AVAILABLE' ? 'var(--green)' : s === 'DEPLOYED' ? 'var(--yellow)' : 'var(--red)';

    return (
        <div className={styles.page}>
            <h2 className={styles.title}>Emergency Units</h2>

            <form onSubmit={handleCreate} className={styles.form}>
                <h3>Register New Unit</h3>
                <div className={styles.formRow}>
                    <input placeholder="Unit Name (e.g. AMB-01)"
                           value={form.name}
                           onChange={e => setForm({...form, name: e.target.value})} required/>
                    <select value={form.type}
                            onChange={e => setForm({...form, type: e.target.value})}>
                        <option>AMBULANCE</option>
                        <option>FIRE_UNIT</option>
                        <option>POLICE_UNIT</option>
                        <option>RESCUE_TEAM</option>
                    </select>
                    <input placeholder="Region (e.g. Abu Dhabi)"
                           value={form.region}
                           onChange={e => setForm({...form, region: e.target.value})} required/>
                </div>
                <button type="submit">Register Unit</button>
            </form>

            <div className={styles.grid}>
                {(units as any[]).map((u: any) => (
                    <div key={u.id} className={styles.card}>
                        <div className={styles.cardHeader}>
                            <span className={styles.unitName}>{u.name}</span>
                            <span className={styles.unitType}>{u.type.replace('_', ' ')}</span>
                        </div>
                        <div className={styles.cardMeta}>
                            <span>📍 {u.region}</span>
                            <span style={{color: statusColor(u.status), fontWeight: 600, fontSize: '0.78rem'}}>
                ● {u.status}
              </span>
                        </div>
                        <div className={styles.cardActions}>
                            {u.status !== 'AVAILABLE' && (
                                <button onClick={() => updateStatus(u.id, 'AVAILABLE')}>
                                    Mark Available
                                </button>
                            )}
                            {u.status !== 'OFFLINE' && (
                                <button className={styles.offlineBtn}
                                        onClick={() => updateStatus(u.id, 'OFFLINE')}>
                                    Take Offline
                                </button>
                            )}
                        </div>
                    </div>
                ))}
            </div>

            <div className={styles.pagination}>
                <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>←</button>
                <span>Page {page + 1} of {totalPages}</span>
                <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>→</button>
            </div>
        </div>
    );
}