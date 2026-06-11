import {useEffect, useState} from 'react';
import api from '../api/axios';
import styles from './Incidents.module.css';

export default function Incidents() {
    const [incidents, setIncidents] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [form, setForm] = useState({
        title: '', description: '', location: '',
        region: '', severity: 'HIGH'
    });

    const fetchIncidents = () => {
        api.get(`/incidents?page=${page}&size=10`).then(r => {
            setIncidents(r.data.content);
            setTotalPages(r.data.totalPages);
        });
    };

    useEffect(() => {
        fetchIncidents();
    }, [page]);

    const handleCreate = async (e: React.FormEvent) => {
        e.preventDefault();
        await api.post('/incidents', form);
        fetchIncidents();
        setForm({title: '', description: '', location: '', region: '', severity: 'HIGH'});
    };

    const updateStatus = async (id: number, status: string) => {
        await api.put(`/incidents/${id}/status?status=${status}`);
        fetchIncidents();
    };

    return (
        <div className={styles.page}>
            <h2 className={styles.title}>Incident Management</h2>

            <form onSubmit={handleCreate} className={styles.form}>
                <h3>Report New Incident</h3>
                <div className={styles.formRow}>
                    <input placeholder="Title" value={form.title}
                           onChange={e => setForm({...form, title: e.target.value})} required/>
                    <input placeholder="Location" value={form.location}
                           onChange={e => setForm({...form, location: e.target.value})} required/>
                    <input placeholder="Region (e.g. Abu Dhabi)" value={form.region}
                           onChange={e => setForm({...form, region: e.target.value})} required/>
                    <select value={form.severity}
                            onChange={e => setForm({...form, severity: e.target.value})}>
                        <option>LOW</option>
                        <option>HIGH</option>
                        <option>CRITICAL</option>
                    </select>
                </div>
                <textarea placeholder="Description" value={form.description}
                          onChange={e => setForm({...form, description: e.target.value})} required/>
                <button type="submit">Submit Report</button>
            </form>

            <div className={styles.list}>
                {(incidents as any[]).map((i: any) => (
                    <div key={i.id} className={styles.card}>
                        <div className={styles.cardHeader}>
                            <span className={styles.cardTitle}>{i.title}</span>
                            <span className={`${styles.severity} ${styles[i.severity.toLowerCase()]}`}>
                {i.severity}
              </span>
                        </div>
                        <p className={styles.desc}>{i.description}</p>
                        <div className={styles.cardFooter}>
                            <span>{i.region} — {i.location}</span>
                            <div className={styles.actions}>
                                <span className={styles.statusBadge}>{i.status}</span>
                                {i.status !== 'RESOLVED' && (
                                    <button onClick={() => updateStatus(i.id,
                                        i.status === 'OPEN' ? 'IN_PROGRESS' : 'RESOLVED')}>
                                        {i.status === 'OPEN' ? 'Start' : 'Resolve'}
                                    </button>
                                )}
                            </div>
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