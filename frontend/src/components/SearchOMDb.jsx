import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom'
import { Pagination, Container, Row, Col, Card, Form, Button, Spinner } from 'react-bootstrap';

export default function App() {
  const [search, setSearch] = useState('');
  const [results, setResults] = useState([]);
  const [type, setType] = useState('');
  const [year, setYear] = useState('');
  const [page, setPage] = useState(1);
  const [totalResults, setTotalResults] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [loading, setLoading] = useState(false);

  const fetchMovies = async () => {
    if(!search) return;
    setLoading(true);
    
    try {
        const url = new URLSearchParams({
            apikey: import.meta.env.VITE_API_KEY,
            s: search,
        });
        
        if(type) url.append('type', type);
        if(year) url.append('year', year);
        if(page) url.append('page', page);
 
        const res = await fetch(`${import.meta.env.VITE_API_URL}?${url.toString()}`);
        const data = await res.json();
        
        setResults([]);

        if(data.Response) {
            setResults(data.Search || []);
            setTotalResults(parseInt(data.totalResults));
            setTotalPages(parseInt(Math.ceil(data.totalResults/10) || 0));
        }

    } catch (error) {
        console.log(error);
    }

    setLoading(false);
  };
  
  useEffect(()=> {
    if(search) {
        fetchMovies()
    }
  }, [page]);

  const handleSearch = (e) => {
    e.preventDefault();
    if(page !== 1) {
        setPage(1);
    }
    fetchMovies();
  }

 return (
  <Container className="py-4 bg-dark text-white min-vh-100">
    <Form
      onSubmit={handleSearch}
      className="mb-4"
    >
      <Row className="g-2 align-items-end">
        <Col xs={12} md={4}>
          <Form.Label>Título</Form.Label>
          <Form.Control
            type="text"
            placeholder="Digite um título"
            value={search}
            onChange={(e) => setSearch(e.target.value)}
          />
        </Col>

        <Col xs={6} md={3}>
          <Form.Label>Tipo</Form.Label>
          <Form.Select value={type} onChange={(e) => setType(e.target.value)}>
            <option value="">Tudo</option>
            <option value="movie">Filmes</option>
            <option value="series">Séries</option>
            <option value="episode">Episódios</option>
          </Form.Select>
        </Col>

        <Col xs={6} md={2}>
          <Form.Label>Ano</Form.Label>
          <Form.Control
            type="number"
            placeholder="Ano"
            value={year}
            onChange={(e) => setYear(e.target.value)}
          />
        </Col>

        <Col xs={12} md={3}>
          <Button
            type="submit"
            variant="primary"
            className="w-100 mt-3 mt-md-0"
          >
            {loading? '...' : 'Buscar'}
          </Button>
        </Col>
      </Row>
    </Form>
    
    {loading && (
        <div className='text-center text-light nt-4'>
            <Spinner animation="border" variant="light"/>
            <p>Buscando...</p>
        </div>
    )}

    <Row>
      {results.map((movie) => (
        <Col key={movie.imdbID} sm={6} md={4} lg={3} className="mb-4">
            <Link to={`/movie/${movie.imdbID}`}>
                <Card className="h-100 bg-secondary text-white">
                <Card.Img variant="top" src={movie.Poster !== "N/A" ? movie.Poster : "https://via.placeholder.com/300x445?text=Sem+Imagem"} />
                <Card.Body>
                <Card.Title>{movie.Title}</Card.Title>
                <Card.Text>{movie.Year}</Card.Text>
                </Card.Body>
                </Card>
          </Link>
        </Col>
      ))}
    </Row>
    {totalResults > 0 && (
        <Pagination className='justify-content-center mt-4'>
            <Pagination.Prev
                onClick={() => setPage((prev)=> Math.max(prev -1, 1))}
                disabled={page === 1}
            />
            <Pagination.Item
                active={page === 1}
                onClick={() => setPage(1)}
            >
                1
            </Pagination.Item>
            {Array.from({length : totalPages }, (_, i) => i + 1)
            .filter((pageIndex) => pageIndex !== 1 && pageIndex !== totalPages && Math.abs(pageIndex - page) <= 3)
            .map((pageIndex) => (
                <Pagination.Item
                    key={pageIndex}
                    active={pageIndex === page}
                    onClick={() => setPage(pageIndex)}
                >
                    {pageIndex}
                </Pagination.Item>
            ))}
            {page < totalPages -3 && <Pagination.Ellipsis disabled />}
            {page !== totalPages && (
                <Pagination.Item onClick={() => setPage(totalPages)}>
                    {totalPages}
                </Pagination.Item>
            )}
            <Pagination.Next
                onClick={() => setPage((prev) => setPage(Math.min(prev + 1, totalPages)))}
                disabled={page === totalPages} 
            />
        </Pagination>
    )}
  </Container>
);
}