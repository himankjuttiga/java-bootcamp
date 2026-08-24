# YAML TODOs

```yaml
# deployment-skeleton.yaml
spec:
  replicas: 2
  template:
    spec:
      securityContext:
        runAsNonRoot: true
        runAsUser: 10001
      containers:
      - name: crm-api
        image: crm-api@sha256:<digest-from-lab41>
        ports:
        - containerPort: 8080
        readinessProbe:
          httpGet:
            path: /actuator/health/readiness
            port: 8080
        resources:
          requests: { cpu: "250m", memory: "384Mi" }
          limits:   { cpu: "500m", memory: "512Mi" }
```

- Label selectors aligned: Service `selector.app=crm-api` matches Pod label `app=crm-api`.
- Do not `kubectl apply` — this exercise is the skeleton only.
